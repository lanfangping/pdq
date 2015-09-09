package uk.ac.ox.cs.pdq.test.cost.estimators;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.ox.cs.pdq.cost.CostParameters.CardinalityEstimationTypes;
import uk.ac.ox.cs.pdq.cost.CostParameters.CostTypes;
import uk.ac.ox.cs.pdq.cost.estimators.WhiteBoxCostEstimator;
import uk.ac.ox.cs.pdq.cost.statistics.estimators.CardinalityEstimator;
import uk.ac.ox.cs.pdq.cost.statistics.estimators.CardinalityEstimatorFactory;
import uk.ac.ox.cs.pdq.db.Schema;
import uk.ac.ox.cs.pdq.io.xml.SchemaReader;
import uk.ac.ox.cs.pdq.logging.performance.StatisticsCollector;
import uk.ac.ox.cs.pdq.plan.DAGPlan;
import uk.ac.ox.cs.pdq.plan.LinearPlan;
import uk.ac.ox.cs.pdq.plan.Plan;

import com.google.common.eventbus.EventBus;

/**
 * Tests the WhiteBox cost estimator
 * @author Efthymia Tsamoura
 *
 */
public class TotalWhiteBoxEstimatorTest extends CostEstimatorTest{

	private EventBus eventBus = new EventBus();
	
	private static String SHEMA_PATH = "test/cost/";
	private static String PLAN_PATH = "test/cost/blackbox/";
	
	String schemata = "schema_bio.xml";
	
	String[] plans = {
			"plan_bio_1.xml",
			"plan_bio_2.xml",
			"plan_bio_3.xml",
			"plan_bio_4.xml",
			"plan_bio_5.xml",
			"plan_bio_6.xml",
			"plan_bio_7.xml",
			"plan_bio_8.xml",
			"plan_bio_9.xml",
			"plan_bio_10.xml",
			"plan_bio_11.xml",
			"plan_bio_12.xml",
			"plan_bio_13.xml",
			"plan_bio_14.xml",
			"plan_bio_15.xml"
			};
	boolean canonicalNames = true;
	String driver = null;
	String url = "jdbc:mysql://localhost/";
	String database = "pdq_chase";
	String username = "root";
	String password ="root";

	@Before
	public void prepare() {

	}

	@Test
	public void test() {

		for(int i = 0; i < this.plans.length; ++i) {
			String s = this.schemata;
			String f = this.plans[i];

			try(FileInputStream sis = new FileInputStream(SHEMA_PATH + s)) {

				Schema schema = new SchemaReader().read(sis);
				if (schema == null) {
					throw new IllegalStateException("Schema must be provided.");
				}
				Plan plan = this.obtainPlan(PLAN_PATH + f, schema);

				CardinalityEstimator card = CardinalityEstimatorFactory.getInstance(CostTypes.BLACKBOX, CardinalityEstimationTypes.NAIVE, schema);

				WhiteBoxCostEstimator costEstimator = null;
				if(plan instanceof DAGPlan) {
					costEstimator = new WhiteBoxCostEstimator<DAGPlan>(new StatisticsCollector(false, this.eventBus), card);
				}
				else {
					costEstimator = new WhiteBoxCostEstimator<LinearPlan>(new StatisticsCollector(false, this.eventBus), card);
				}
			
				Assert.assertEquals(plan.getCost(), costEstimator.estimateCost(plan));

			} catch (FileNotFoundException e) {
				System.out.println("Cannot find input files");
			} catch (Exception e) {
				System.out.println("EXCEPTION: " + e.getClass().getSimpleName() + " " + e.getMessage());
				e.printStackTrace();
			} catch (Error e) {
				System.out.println("ERROR: " + e.getClass().getSimpleName() + " " + e.getMessage());
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

}