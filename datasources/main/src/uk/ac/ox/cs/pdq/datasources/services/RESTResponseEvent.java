// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.datasources.services;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import uk.ac.ox.cs.pdq.datasources.AccessException;
import uk.ac.ox.cs.pdq.datasources.services.RequestEvent;
import uk.ac.ox.cs.pdq.datasources.services.ResponseEvent;


// RESTResponseEvent occurs immediately after a REST access event
public class RESTResponseEvent implements ResponseEvent {

	public static enum RESTResponseStatus{
		SUCCESS,
		FAILURE};

		private final RESTRequestEvent request;

		private final Response response;

		private String violationMessage = null;

		public RESTResponseEvent(RESTRequestEvent request, Response response) throws AccessException, ProcessingException {
			super();
			this.request = request;
			this.response = response;
		}


		public RESTRequestEvent getRequest() {
			return this.request;
		}

		@Override
		public void setUsageViolationMessage(String msg) {
			this.violationMessage = msg;
		}

		@Override
		public String getUsageViolationMessage() {
			return this.violationMessage;
		}

		@Override
		public boolean hasUsageViolationMessage() {
			return this.violationMessage != null;
		}

		public Response getResponse() {
			return this.response;
		}


		@Override
		public boolean hasMoreRequestEvents() {
			return false;
		}


		@Override
		public RequestEvent nextRequestEvent() {
			return new RESTRequestEvent(request.getTarget(), request.getMediaType());
		}
}
