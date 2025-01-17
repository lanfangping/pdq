// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.datasources.services;

import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import uk.ac.ox.cs.pdq.datasources.AccessException;
import uk.ac.ox.cs.pdq.datasources.ExecutableAccessMethod;
import uk.ac.ox.cs.pdq.datasources.services.service.RESTExecutableAccessMethodAttributeSpecification;
import uk.ac.ox.cs.pdq.datasources.services.service.RESTExecutableAccessMethodSpecification;
import uk.ac.ox.cs.pdq.datasources.services.service.Service;
import uk.ac.ox.cs.pdq.datasources.services.service.StaticAttribute;
import uk.ac.ox.cs.pdq.datasources.services.servicegroup.AttributeEncoding;
import uk.ac.ox.cs.pdq.datasources.services.servicegroup.ServiceGroup;
import uk.ac.ox.cs.pdq.datasources.tuple.Table;
import uk.ac.ox.cs.pdq.db.Attribute;
import uk.ac.ox.cs.pdq.db.Relation;
import uk.ac.ox.cs.pdq.db.tuple.Tuple;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Mark Ridler
 *
 */
// RESTAccessMethod is the implementation which calls REST for a defined access method
public class RESTAccessMethod extends ExecutableAccessMethod {

	private static final long serialVersionUID = 1L;
	
	private ServiceGroup sgr;
	private Service sr;
	private RESTExecutableAccessMethodSpecification am;
	private String url;
	private String template;
	private WebTarget target;
	private MediaType mediaType;
	private JsonResponseUnmarshaller jsonResponseUnmarshaller;
	private XmlResponseUnmarshaller xmlResponseUnmarshaller;
	private Attribute[] inputattributes;
	private Attribute[] outputattributes;
	private TreeMap<String, AttributeEncoding> attributeEncodingMap;
	
	public RESTAccessMethod(String name, Attribute[] attributes, Integer[] inputs, Relation relation,
			Map<Attribute, Attribute> attributeMapping, WebTarget target, MediaType mediaType,
			ServiceGroup sgr, Service sr, RESTExecutableAccessMethodSpecification am) {
		super(name, attributes, inputs, relation, attributeMapping);
		this.target = target;
		this.mediaType = mediaType;
		this.sgr = sgr;
		this.sr = sr;
		this.am = am;
		
		// Setup response unmarshallers
		jsonResponseUnmarshaller = new JsonResponseUnmarshaller(attributes, sr.getResultDelimiter());
		xmlResponseUnmarshaller = new XmlResponseUnmarshaller(attributes, sr.getResultDelimiter());
	}

	@Override
	public void close() {
		
	}

	@Override
	public boolean isClosed() throws Exception {
		return false;
	}
	
	// Conversion from string to type ... there may be a better way of doing this
	private Type typeType(String type)
	{
		if(type.equals("String"))
		{
			return String.class;
		}
		else if(type.equals("Integer"))
		{
			return Integer.class;
		}
		else if(type.equals("Double"))
		{
			return Double.class;
		}
		else if(type.equals("Boolean"))
		{
			return Boolean.class;
		}
		return null;
	}
	
	// Process a single tuple of input, reformatting the URL as we go
	private void processInput(Tuple tuple)
	{
		this.url = sr.getUrl();
		this.template = "";
		this.attributeEncodingMap = new TreeMap<String, AttributeEncoding>();

		// Setup the attributeEncodingMap by putting in all AttributeEncodings from the ServiceGroupsRoot object
		for(AttributeEncoding ae: sgr.getAttributeEncoding()) if(ae.getName() != null) attributeEncodingMap.put(ae.getName(), ae);

		// Format the templates stored in the AttributeEncodings from the ServiceGroupsRoot object 
		formatTemplate(sgr, sr, am);

		// Setup inputs, outputs, uri and params
		LinkedList<Attribute> inputs = new LinkedList<Attribute>();
		LinkedList<Attribute> outputs = new LinkedList<Attribute>();
		StringBuilder uri = new StringBuilder(this.url);
		Map<String, Object> params = new TreeMap<String, Object>();

		// Parse the static and non-static attributes from the ServiceRoot object, phase1
		mapAttributesPhase1(sr, am, inputs, outputs, uri, params, tuple);

		// Apply the template by appending its fully populated format onto the uri
		if(this.template != null) uri.append(this.template);

		// Setup the web target
		this.target = ClientBuilder.newClient().register(JacksonFeatures.class).target(uri.toString());

		// Parse the static and non-static attributes from the ServiceRoot object, phase1
		mapAttributesPhase2(sr, am, tuple);

		// Copy attributes
		inputattributes = new Attribute[inputs.size()];
		for(int i = 0; i < inputs.size(); i++) inputattributes[i] = inputs.get(i);
		outputattributes = new Attribute[outputs.size()];
		for(int i = 0; i < outputs.size(); i++) outputattributes[i] = outputs.get(i);
	}
	
	// Format a list of templates as presented by the AttributeEncodings
	private void formatTemplate(ServiceGroup sgr, Service sr, RESTExecutableAccessMethodSpecification am)
	{
		String result = "";
		TreeMap<AttributeEncoding, String> attributeEncodingMap2 = new TreeMap<AttributeEncoding, String>();
		if(sr.getStaticAttribute() != null)
		{
			for(StaticAttribute sa: sr.getStaticAttribute())
			{
				String encoding = sa.getAttributeEncoding();
				String index = sa.getAttributeEncodingIndex();
				formatTemplateProcessParams(encoding, index, sa.getValue(), attributeEncodingMap2);
			}
		}
		if(am.getAttributes() != null)
		{
			for(RESTExecutableAccessMethodAttributeSpecification aa: am.getAttributes())
			{
				String encoding = aa.getAttributeEncoding();
				String index = aa.getAttributeEncodingIndex();	
				formatTemplateProcessParams(encoding, index, aa.getValue(), attributeEncodingMap2);		
			}
		}
		Collection<String> cs = attributeEncodingMap2.values();
		for(String s: cs) result += s;
		this.template = result;
	}

	// Do the donkey work for formatTemplate()
	public void formatTemplateProcessParams(String encoding, String index, String value, TreeMap<AttributeEncoding, String> attributeEncodingMap2)
	{
		if(value == null) value="";
		if(encoding != null)
		{
			AttributeEncoding ae;
			if((ae = attributeEncodingMap.get(encoding)) != null)
			{
				String template;
				if((template = attributeEncodingMap2.get(ae)) != null)
				{
					if(index != null)
					{
						template = template.replace("{" + index + "}", value);
						attributeEncodingMap2.remove(ae);
						attributeEncodingMap2.put(ae, template);
					}
				}
				else
				{
					if((template = ae.getTemplate()) != null)
					{
						if(index != null)
						{
							template = template.replace("{" + index + "}", value);
						}
						attributeEncodingMap2.put(ae, template);
					}
				}
			}
		}
	}

	// Phase 1 builds structures and processes path-elements
	private void mapAttributesPhase1(Service sr, RESTExecutableAccessMethodSpecification am, List<Attribute> inputs, List<Attribute> outputs, StringBuilder uri, Map<String, Object> params, Tuple tuple)
	{
		if(sr.getStaticAttribute() != null)
		{
			for(StaticAttribute sa : sr.getStaticAttribute())
			{
				mapAttributesPhase1ProcessParams(sa.getAttributeEncoding(), sa.getName(), sa.getType(), sa.getValue(), inputs, uri, params, null);
			}
		}
		if(am.getAttributes() != null)
		{
			int a = 0;
			for(RESTExecutableAccessMethodAttributeSpecification aa : am.getAttributes())
			{
				if((aa.getInput() != null) && aa.getInput().equals("true"))
				{
					mapAttributesPhase1ProcessParams(aa.getAttributeEncoding(), aa.getName(), aa.getType(), aa.getValue(), inputs, uri, params, (tuple != null) ? (tuple.getValue(a)).toString() : null);
					a++;
				}
				if((aa.getOutput() != null) && aa.getOutput().equals("true"))
				{
					outputs.add(Attribute.create(typeType(aa.getType()), aa.getName()));
				}
			}
		}
	}
	
	// Do the donkey work for mapAttributesPhase1()
	public void mapAttributesPhase1ProcessParams(String encoding, String name, String type, String value, List<Attribute> inputs, StringBuilder uri, Map<String, Object> params, String tuplevalue)
	{
		if((name != null) && (type != null))
		{
			inputs.add(Attribute.create(typeType(type), name));
			if(encoding != null)
			{
				AttributeEncoding ae = attributeEncodingMap.get(encoding);
				if(ae != null)
				{
					if((ae.getType() != null) && ae.getType().equals("path-element"))
					{
						if(tuplevalue != null) value = tuplevalue;
						if(value != null)
						{
							if(this.template == null)
							{
								uri.append("/" + value);
							}
							else
							{
								params.put(name, value);
							}
						}
					}
				}
			}
		}
	}
	
	// Phase 2 processes the name/value pairs, adding them onto the web target
	private void mapAttributesPhase2(Service sr, RESTExecutableAccessMethodSpecification am, Tuple tuple)
	{
		if(sr.getStaticAttribute() != null)
		{
			for(StaticAttribute sa : sr.getStaticAttribute())
			{
				mapAttributesPhase2ProcessParams(sa.getAttributeEncoding(), sa.getName(), sa.getValue(), null);
			}
		}
		if(am.getAttributes() != null)
		{
			int a = 0;
			for(RESTExecutableAccessMethodAttributeSpecification aa : am.getAttributes())
			{
				if((aa.getInput() != null) && aa.getInput().equals("true"))
				{
					mapAttributesPhase2ProcessParams(aa.getAttributeEncoding(), aa.getName(), aa.getValue(), tuple.getValue(a).toString());
				}
			}
		}
	}
	
	// Do the donkey work for mapAttributesPhase2()
	public void mapAttributesPhase2ProcessParams(String encoding, String name, String value, String tuplevalue)
	{
		if(encoding != null)
		{
			AttributeEncoding ae;
			if((ae = attributeEncodingMap.get(encoding)) != null)
			{
				if((ae.getType() != null) && ae.getType().equals("url-param"))
				{
					if(name != null)
					{
						if(tuplevalue != null)
						{
							this.target = target.queryParam(name, tuplevalue);
						}
						else if(value != null)
						{
							this.target = target.queryParam(name, value);
						}
						else if(ae.getValue() != null)
						{
							this.target = target.queryParam(name, ae.getValue());								
						}
					}
					else if(ae.getName() != null)
					{
						if(tuplevalue != null)
						{
							this.target = target.queryParam(ae.getName(), tuplevalue);
						}
						else if(value != null)
						{
							this.target = target.queryParam(ae.getName(), value);
						}
						else if(ae.getValue() != null)
						{
							this.target = target.queryParam(ae.getName(), ae.getValue());								
						}						
					}
				}
			}
		}
	}
	

	// Perform the main access to the REST protocol and parse the results
	public Table accessTable()
	{
		// Setup a RESTRequestEvent from web target and mediaType
		RESTRequestEvent request = new RESTRequestEvent(target, mediaType);
		
		// Process the RESTRequestEvent to generate a RESTResponseEvent
		RESTResponseEvent response = request.processRequest();

		// Create a new table as input for the unmarshallers
		Table table = new Table();
		// Process the HTTP response and call response unmarshallers if appropriate
		int status = response.getResponse().getStatus();
		if (status == 200) {
			if(mediaType.getType().equals("application") || mediaType.getType().equals("text"))
			{
				if(mediaType.getSubtype().equals("xml") || mediaType.getSubtype().equals("plain")) return xmlResponseUnmarshaller.unmarshalXml(response.getResponse(), table);
				else if(mediaType.getSubtype().equals("json")) return jsonResponseUnmarshaller.unmarshalJson(response.getResponse(), table); 
			}
		} else if ((status == 400) || (status == 404) || (status == 406)) {
			throw new AccessException(status
					+ " - " + response.getResponse().getStatusInfo().getReasonPhrase()
					+ "\n" + response.getResponse().readEntity(String.class));
		} else {
			throw new AccessException(status
					+ " - " + response.getResponse().getStatusInfo().getReasonPhrase()
					+ "\n" + response.getResponse().readEntity(String.class));
		}
		return new Table();		
	}
	
	// The stream implementation needs revisiting because it is wholly in memory
	@Override
	protected Stream<Tuple> fetchTuples(Iterator<Tuple> inputTuples) {
		LinkedList<Stream<Tuple>> list = new LinkedList<Stream<Tuple>>();
		Stream<Tuple> result = Stream.empty();
		if(inputTuples == null)
		{
			processInput(null);
			Table t = accessTable();
			list.add(StreamSupport.stream(t.spliterator(), false));
		}
		else
		{
			while(inputTuples.hasNext())
			{
				Tuple tuple = inputTuples.next();
				processInput(tuple);
				Table t = accessTable();
				list.add(StreamSupport.stream(t.spliterator(), false));
			}
		}
		for(int i = 0; i < list.size(); i++)
		{
			result = Stream.concat(result, list.get(i));
		}
		return result;
	}
	
	// This is specifically not an override
	public Table accessTable(Tuple tuple)
	{
		processInput(tuple);
		return accessTable();
	}
}