package applications;

import es.us.isa.ppinot.evaluation.Aggregator;
import es.us.isa.ppinot.model.DataContentSelection;
import es.us.isa.ppinot.model.MeasureDefinition;
import es.us.isa.ppinot.model.TimeUnit;
import es.us.isa.ppinot.model.aggregated.AggregatedMeasure;
import es.us.isa.ppinot.model.base.CountMeasure;
import es.us.isa.ppinot.model.base.DataMeasure;
import es.us.isa.ppinot.model.base.TimeMeasure;
import es.us.isa.ppinot.model.condition.TimeInstantCondition;
import es.us.isa.ppinot.model.condition.TimeMeasureType;
import es.us.isa.ppinot.model.derived.DerivedMeasure;
import es.us.isa.ppinot.model.derived.DerivedMultiInstanceMeasure;
import es.us.isa.ppinot.model.derived.DerivedSingleInstanceMeasure;
import es.us.isa.ppinot.model.state.DataObjectState;
import es.us.isa.ppinot.model.state.GenericState;
import pappi.BoundaryEstimator;
import pappi.PrivacyAwareAggregatedMeasure;
import pappi.PrivacyAwareAggregator;

public class CaseStudyMeasureDefinitions {
	//General guidances
	//	for SUM and AVG - use laplace mechanism (SUM_LAP and AVG_LAP)
	//	for MIN and MAX - use interval mechanism (MIN_EXP and MAX_EXP) with BoundaryEstimation.EXTEND and extensionFactor around 1.5
	//  for privatized measures, all aggregationmeasures need to be privatizedAggregationMeasures
	
	//TODO wait for fix on data measures...
	static public MeasureDefinition buildPPI1(boolean privatize) {
		if (privatize) {
			return null;
		}
		else {
			DataMeasure leftAgainstMedicalAdvice = new DataMeasure();
			leftAgainstMedicalAdvice.setDataContentSelection(new DataContentSelection("TYPE OF DISCHARGE", ""));
			
			return leftAgainstMedicalAdvice;
		}
	}
	
	
	
	//Time PPIs - Time to treatment
	static public MeasureDefinition buildPPI2(boolean privatize) {
		if (privatize) {
			//get time from triage (i.e. beginning of each trace to discharge event)
			TimeMeasure t = new TimeMeasure();
			t.setFrom(new TimeInstantCondition("TRIAGE", GenericState.END));
			t.setTo(new TimeInstantCondition("DISCHARGE", GenericState.END));
			t.setUnitOfMeasure(TimeUnit.MINUTES);
	
			//get mean of all times using laplace mechanism on mean, epsilon=0.1 and boundary as min and max values of time frame
			PrivacyAwareAggregatedMeasure mean = new PrivacyAwareAggregatedMeasure();
			mean.setBaseMeasure(t);
			mean.setAggregationFunction(PrivacyAwareAggregator.AVG_LAP);
			mean.setEpsilon(0.1);
			mean.setBoundaryEstimation(BoundaryEstimator.MINMAX);
			
			return mean;
		}
		else {
			//get time from triage (i.e. beginning of each trace to discharge event)
			TimeMeasure t = new TimeMeasure();
			t.setFrom(new TimeInstantCondition("TRIAGE", GenericState.END));
			t.setTo(new TimeInstantCondition("DISCHARGE", GenericState.END));
			t.setUnitOfMeasure(TimeUnit.MINUTES);
	
			//get mean of all times
			AggregatedMeasure mean = new AggregatedMeasure();
			mean.setBaseMeasure(t);
			mean.setAggregationFunction(Aggregator.AVG);
			
			return mean;
		}
	}
	
	
	
	//Resource Utilization - Number of plain radiography studies
	static public MeasureDefinition buildPPI3(boolean privatize) {
		if (privatize) {
			//first sum up number of total radiological exams
			CountMeasure noRadiologicalExams = new CountMeasure();
			noRadiologicalExams.setWhen(new TimeInstantCondition("RADIOLOGICAL EXAM REQUEST", GenericState.END));

			//privatize sum using laplacem echanism, epsilon=0.1 and minimum and maximum element in timeframe as boundaries
			PrivacyAwareAggregatedMeasure RadiologicalExamsSum = new PrivacyAwareAggregatedMeasure();
			RadiologicalExamsSum.setBaseMeasure(noRadiologicalExams);
			RadiologicalExamsSum.setAggregationFunction(PrivacyAwareAggregator.SUM_LAP);
			RadiologicalExamsSum.setEpsilon(0.1);
			RadiologicalExamsSum.setBoundaryEstimation(BoundaryEstimator.MINMAX);
			
			
			//get number of process instances
			CountMeasure total = new CountMeasure();
			total.setWhen(new TimeInstantCondition("TRIAGE", GenericState.END));	
			
			//dirty hack - returns 1 for each trace, there may be a better way to do this 
			DerivedSingleInstanceMeasure staticPerTrace = new DerivedSingleInstanceMeasure();
			staticPerTrace.setFunction("x/x");
			staticPerTrace.addUsedMeasure("x", total);
			
			PrivacyAwareAggregatedMeasure totalSum = new PrivacyAwareAggregatedMeasure();
			totalSum.setBaseMeasure(staticPerTrace);
			totalSum.setAggregationFunction(Aggregator.SUM);	
			
			//divide number of radiological exams by number of process instances
			DerivedMultiInstanceMeasure xPerPerson = new DerivedMultiInstanceMeasure();
			xPerPerson.setFunction("a/b");
			xPerPerson.addUsedMeasure("a", RadiologicalExamsSum);
			xPerPerson.addUsedMeasure("b", totalSum);
			
			return xPerPerson;
		}
		else {
			//first sum up number of total radiological exams
			CountMeasure noRadiologicalExams = new CountMeasure();
			noRadiologicalExams.setWhen(new TimeInstantCondition("RADIOLOGICAL EXAM REQUEST", GenericState.END));

			AggregatedMeasure RadiologicalExamsSum = new AggregatedMeasure();
			RadiologicalExamsSum.setBaseMeasure(noRadiologicalExams);
			RadiologicalExamsSum.setAggregationFunction(Aggregator.SUM);
			
			//get number of process instances
			CountMeasure total = new CountMeasure();
			total.setWhen(new TimeInstantCondition("TRIAGE", GenericState.END));	
			
			//dirty hack - returns 1 for each trace, there may be a better way to do this 
			DerivedSingleInstanceMeasure staticPerTrace = new DerivedSingleInstanceMeasure();
			staticPerTrace.setFunction("x/x");
			staticPerTrace.addUsedMeasure("x", total);
			
			AggregatedMeasure totalSum = new AggregatedMeasure();
			totalSum.setBaseMeasure(staticPerTrace);
			totalSum.setAggregationFunction(Aggregator.SUM);	
			
			//divide number of radiological exams by number of process instances
			DerivedMultiInstanceMeasure xPerPerson = new DerivedMultiInstanceMeasure();
			xPerPerson.setFunction("a/b");
			xPerPerson.addUsedMeasure("a", RadiologicalExamsSum);
			xPerPerson.addUsedMeasure("b", totalSum);
			
			return xPerPerson;
		}
	}
}
