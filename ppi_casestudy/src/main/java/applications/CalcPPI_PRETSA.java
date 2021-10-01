package applications;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import es.us.isa.ppinot.evaluation.Aggregator;
import es.us.isa.ppinot.evaluation.Measure;
import es.us.isa.ppinot.evaluation.TemporalMeasureScope;
import es.us.isa.ppinot.evaluation.evaluators.MeasureEvaluator;
import es.us.isa.ppinot.evaluation.logs.LogProvider;
import es.us.isa.ppinot.evaluation.logs.MXMLLog;
import es.us.isa.ppinot.model.DataContentSelection;
import es.us.isa.ppinot.model.MeasureDefinition;
import es.us.isa.ppinot.model.TimeUnit;
import es.us.isa.ppinot.model.aggregated.AggregatedMeasure;
import es.us.isa.ppinot.model.base.CountMeasure;
import es.us.isa.ppinot.model.base.DataMeasure;
import es.us.isa.ppinot.model.base.TimeMeasure;
import es.us.isa.ppinot.model.condition.TimeInstantCondition;
import es.us.isa.ppinot.model.derived.DerivedMultiInstanceMeasure;
import es.us.isa.ppinot.model.derived.DerivedSingleInstanceMeasure;
import es.us.isa.ppinot.model.scope.Period;
import es.us.isa.ppinot.model.scope.SimpleTimeFilter;
import es.us.isa.ppinot.model.state.GenericState;
import pappi.BoundaryEstimator;
import pappi.PrivacyAwareAggregatedMeasure;
import pappi.PrivacyAwareAggregator;
import pappi.PrivacyAwareLogMeasureEvaluator;
	
public class CalcPPI_PRETSA {
	public static void main(String[] args) throws Exception {
		double eps = 0.1;
		if (args.length > 0) {
			try {
				eps = Double.parseDouble(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("Argument epsilon must me a double");
			}
		}
		
		CalcPPI_PRETSA app = new CalcPPI_PRETSA();
		List<Measure> privatizedMeasures = null;
		List<Measure> trueMeasures = null;
		
		int repetitions = 10;
	    BufferedWriter writer = new BufferedWriter(new FileWriter("evaluation_hospital.csv"));
	    writer.write("Kpi;From;To;Algorithm;NoOfValues;Result;Orig\n");
		LogProvider log = null;
		MXMLLog eventLog;
		
		// Number of RADIOLOGICAL EXAM REQUESTs
		eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
		trueMeasures = app.computePPI(eventLog, app.buildNoOfRequests("RADIOLOGICAL EXAM REQUEST", false, eps));
		privatizedMeasures = trueMeasures;		 // nur für den Test
		writeMeasuresToFile("NoOfRadiologicalExamRequests", trueMeasures, privatizedMeasures, repetitions, writer); // nur für den Test

/*
		for (int i=0; i<repetitions; i++) {
			System.out.println("Evaluating NoOfRadiologicalExamRequests - Repetition "+i);
			eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
			privatizedMeasures = app.computePPI(eventLog, app.buildNoOfRequests("RADIOLOGICAL EXAM REQUEST", true, eps));
			
			writeMeasuresToFile("NoOfRadiologicalExamRequests", trueMeasures, privatizedMeasures, repetitions, writer);
		}
*/
		
/*
		// Number of LAB REQUESTs
		eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
		trueMeasures = app.computePPI(eventLog, app.buildNoOfRequests("LAB REQUEST", false, eps));
		
		for (int i=0; i<repetitions; i++) {
			System.out.println("Evaluating NoOfLabRequests - Repetition "+i);
			eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
			privatizedMeasures = app.computePPI(eventLog, app.buildNoOfRequests("LAB REQUEST", true, eps));
			
			writeMeasuresToFile("NoOfLabRequests", trueMeasures, privatizedMeasures, repetitions, writer);
		}
		
		
		// Number of SPECIALIST REQUESTs
		eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
		trueMeasures = app.computePPI(eventLog, app.buildNoOfRequests("SPECIALIST REQUEST", false, eps));
		
		for (int i=0; i<repetitions; i++) {
			System.out.println("Evaluating NoOfSpecialistRequests - Repetition "+i);
			eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
			privatizedMeasures = app.computePPI(eventLog, app.buildNoOfRequests("SPECIALIST REQUEST", true, eps));
			
			writeMeasuresToFile("NoOfSpecialistRequests", trueMeasures, privatizedMeasures, repetitions, writer);
		}
		
		
		// Number of Ultrasound Scans
		eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
		trueMeasures = app.computePPI(eventLog, app.buildNoOfActivityAttributes("ACTIVITY ATTRIBUTE", "Ultrasound Scan", false, eps));
		
		for (int i=0; i<repetitions; i++) {
			System.out.println("Evaluating NoOfUltraScans - Repetition "+i);
			eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
			privatizedMeasures = app.computePPI(eventLog, app.buildNoOfActivityAttributes("ACTIVITY ATTRIBUTE", "Ultrasound Scan", true, eps));
			
			writeMeasuresToFile("NoOfUltraSoundScans", trueMeasures, privatizedMeasures, repetitions, writer);
		}
		
		
		// Number of CT-Scans
		eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
		trueMeasures = app.computePPI(eventLog, app.buildNoOfActivityAttributes("ACTIVITY ATTRIBUTE", "CT Scan", false, eps));
		
		for (int i=0; i<repetitions; i++) {
			System.out.println("Evaluating NoOfCTScans - Repetition "+i);
			eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
			privatizedMeasures = app.computePPI(eventLog, app.buildNoOfActivityAttributes("ACTIVITY ATTRIBUTE", "CT Scan", true, eps));
			
			writeMeasuresToFile("NoOfCTScans", trueMeasures, privatizedMeasures, repetitions, writer);
		}
		
		
		// Number of MRIs
		eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
		trueMeasures = app.computePPI(eventLog, app.buildNoOfActivityAttributes("ACTIVITY ATTRIBUTE", "MRI", false, eps));
		
		for (int i=0; i<repetitions; i++) {
			System.out.println("Evaluating NoOfMRIs - Repetition "+i);
			eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
			privatizedMeasures = app.computePPI(eventLog, app.buildNoOfActivityAttributes("ACTIVITY ATTRIBUTE", "MRI", true, eps));
			
			writeMeasuresToFile("NoOfMRIs", trueMeasures, privatizedMeasures, repetitions, writer);
		}	
		
		
		// Number of Discharges before completion of treatment
		eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
		trueMeasures = app.computePPI(eventLog, app.buildNoOfActivityAttributes("TYPE OF DISCHARGE", "Left before completion of treatment", false, eps));
		
		for (int i=0; i<repetitions; i++) {
			System.out.println("Evaluating NoOfLeftEarly - Repetition "+i);
			eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
			privatizedMeasures = app.computePPI(eventLog, app.buildNoOfActivityAttributes("TYPE OF DISCHARGE", "Left before completion of treatment", true, eps));
			
			writeMeasuresToFile("NoOfLeftBeforeCompletionOfTreatment", trueMeasures, privatizedMeasures, repetitions, writer);
		}	

		
		// Number of Discharges against medical advice
		eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
		trueMeasures = app.computePPI(eventLog, app.buildNoOfActivityAttributes("TYPE OF DISCHARGE", "Left against medical advice", false, eps));
		
		for (int i=0; i<repetitions; i++) {
			System.out.println("Evaluating NoOfLeftAgainstMedicalAdvice - Repetition "+i);
			eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
			privatizedMeasures = app.computePPI(eventLog, app.buildNoOfActivityAttributes("TYPE OF DISCHARGE", "Left against medical advice", true, eps));
			
			writeMeasuresToFile("NoOfLeftAgainstMedicalAdvice", trueMeasures, privatizedMeasures, repetitions, writer);
		}	
		
		
		// Number of Discharges without notification to ED operators
		eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
		trueMeasures = app.computePPI(eventLog, app.buildNoOfActivityAttributes("TYPE OF DISCHARGE", "Voluntary left without notification to ED operators", false, eps));
		
		for (int i=0; i<repetitions; i++) {
			System.out.println("Evaluating NoOfLeftWithoutNotification - Repetition "+i);
			eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
			privatizedMeasures = app.computePPI(eventLog, app.buildNoOfActivityAttributes("TYPE OF DISCHARGE", "Voluntary left without notification to ED operators", true, eps));
			
			writeMeasuresToFile("NoOfLeftWithoutNotification", trueMeasures, privatizedMeasures, repetitions, writer);
		}	
		
		
		// Length of stay for discharged patients
		eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
		trueMeasures = app.computePPI(eventLog, app.buildLenOfStayDischarged(false, eps));
		
		for (int i=0; i<repetitions; i++) {
			System.out.println("Evaluating LenOfStayDischarged - Repetition "+i);
			eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
			privatizedMeasures = app.computePPI(eventLog, app.buildLenOfStayDischarged(true, eps));
			
			writeMeasuresToFile("buildLenOfStayDischarged", trueMeasures, privatizedMeasures, repetitions, writer);
		}

		
		// Radiology duration
		eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
		trueMeasures = app.computePPI(eventLog, app.buildRadiologyDuration(false, eps));
		
		for (int i=0; i<repetitions; i++) {
			System.out.println("Evaluating RadiologyDuration - Repetition "+i);
			eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
			privatizedMeasures = app.computePPI(eventLog, app.buildRadiologyDuration(true, eps));
			
			writeMeasuresToFile("buildRadiologyDuration", trueMeasures, privatizedMeasures, repetitions, writer);
		}
		
		
		// Lab duration
		eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
		trueMeasures = app.computePPI(eventLog, app.buildLabDuration(false, eps));
		
		for (int i=0; i<repetitions; i++) {
			System.out.println("Evaluating LabDuration - Repetition "+i);
			eventLog = new MXMLLog(new FileInputStream(new File("F:\\Dokumente[F]\\SHK\\PPI\\casestudy\\ppi_casestudy\\Dataset\\All_Severity_codes.mxml")),null);
			privatizedMeasures = app.computePPI(eventLog, app.buildLabDuration(true, eps));
			
			writeMeasuresToFile("buildLabDuration", trueMeasures, privatizedMeasures, repetitions, writer);
		}
	
		
*/		
		writer.close();
	}
	
	
	private static void writeMeasuresToFile(String kpi, List<Measure> trueMeasures, List<Measure> privatizedMeasures,
			int repetitions, BufferedWriter writer) throws IOException {
		//add all time scopes from the measures
		List<TemporalMeasureScope> timeScopes = new ArrayList<TemporalMeasureScope>();
		trueMeasures.stream().forEach(x -> timeScopes.add((TemporalMeasureScope)x.getMeasureScope()));
		privatizedMeasures.stream().forEach(x -> timeScopes.add((TemporalMeasureScope)x.getMeasureScope()));
		
		//order timescopes and remove duplicates
		List<TemporalMeasureScope> orderedTimeScopes = timeScopes.stream().collect(Collectors.toList());
		orderedTimeScopes.sort((a, b) -> a.getStart().compareTo(b.getStart()));
		for (int i=orderedTimeScopes.size()-1;i>0 ;i--) {
			TemporalMeasureScope m = orderedTimeScopes.get(i);
			TemporalMeasureScope prior = orderedTimeScopes.get(i-1);
			if (m.getStart().equals(prior.getStart()) && m.getEnd().equals(prior.getEnd())) {
				orderedTimeScopes.remove(i);
			}
		}
		//orderedTimeScopes.stream().forEach(x -> System.out.println(x.getStart()+" "+x.getEnd()));
		
		//for each timescope, get data from measures and/or trueMeasures
		for(TemporalMeasureScope t : orderedTimeScopes) {
			
			//use Double for explicit modelling of NaN
			Double trueMeasureValue = Double.NaN;
			
			Double privatizedMeasureSize = Double.NaN;
			Double privatizedMeasureValue = Double.NaN;
			//System.out.println(t.getStart()+" -> "+t.getEnd());
			
			for(Measure m : privatizedMeasures) {
				if (((TemporalMeasureScope)m.getMeasureScope()).getStart().equals(t.getStart()) && ((TemporalMeasureScope)m.getMeasureScope()).getEnd().equals(t.getEnd())) {
					if (!new Double(m.getValue()).isNaN()) {
						//System.out.println("Privatized: " + m.getValue());
						privatizedMeasureSize = (double)m.getInstances().size();
						privatizedMeasureValue = m.getValue();
						//System.out.println(privatizedMeasureSize);
						break;
					}
				}
			}
			for(Measure m : trueMeasures) {
				if (((TemporalMeasureScope)m.getMeasureScope()).getStart().equals(t.getStart()) && ((TemporalMeasureScope)m.getMeasureScope()).getEnd().equals(t.getEnd())) {
					if (!new Double(m.getValue()).isNaN()) {
					//System.out.println("Original: "+m.getValue());
					trueMeasureValue = m.getValue();
					break;
					}
				}
			}

			
			//write data into file
			writer.write(String.join(";",
					kpi,
					t.getStart().toString(),
					t.getEnd().toString(),
					"PrivatizedMeasures",
					privatizedMeasureSize.isNaN()?"NaN":privatizedMeasureSize.toString(),
					privatizedMeasureValue.isNaN()?"NaN":privatizedMeasureValue.toString(),
					trueMeasureValue.isNaN()?"NaN":trueMeasureValue.toString()
					));
			writer.write("\n");
		}
	}
	
	private List<Measure> computePPI(LogProvider log, MeasureDefinition measure) throws Exception {
		//make sure normal queries can also be answered without changing Evaluator
		MeasureEvaluator evaluator = new PrivacyAwareLogMeasureEvaluator(log);
		return evaluator.eval(measure, new SimpleTimeFilter(Period.MONTHLY,1, false));
	}
	
	
	
	/* Number of radiography studies / lab requests / specialist requests
	 * @param request {"RADIOLOGICAL EXAM REQUEST", "LAB REQUEST", "SPECIALIST REQUEST"}
	*/
	public MeasureDefinition buildNoOfRequests(String request, boolean privatize, double epsilon) {
		if (privatize) {
			//first sum up number of total requests
			CountMeasure noRequests = new CountMeasure();
			noRequests.setWhen(new TimeInstantCondition(request, GenericState.END));

			//privatize sum using laplace mechanism and minimum and maximum element in timeframe as boundaries
			PrivacyAwareAggregatedMeasure RequestsSum = new PrivacyAwareAggregatedMeasure();
			RequestsSum.setBaseMeasure(noRequests);
			RequestsSum.setAggregationFunction(PrivacyAwareAggregator.SUM_LAP);
			RequestsSum.setEpsilon(epsilon);
			RequestsSum.setBoundaryEstimation(BoundaryEstimator.MINMAX);
			
			
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
			
			//divide number of requests by number of process instances
			DerivedMultiInstanceMeasure xPerPerson = new DerivedMultiInstanceMeasure();
			xPerPerson.setFunction("a/b");
			xPerPerson.addUsedMeasure("a", RequestsSum);
			xPerPerson.addUsedMeasure("b", totalSum);
			
			return xPerPerson;
		}
		else {
			//first sum up number of total requests
			CountMeasure noRequests = new CountMeasure();
			noRequests.setWhen(new TimeInstantCondition(request, GenericState.END));

			AggregatedMeasure RequestsSum = new AggregatedMeasure();
			RequestsSum.setBaseMeasure(noRequests);
			RequestsSum.setAggregationFunction(Aggregator.SUM);
			
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
			
			//divide number of requests by number of process instances
			DerivedMultiInstanceMeasure xPerPerson = new DerivedMultiInstanceMeasure();
			xPerPerson.setFunction("a/b");
			xPerPerson.addUsedMeasure("a", RequestsSum);
			xPerPerson.addUsedMeasure("b", totalSum);
			
			return xPerPerson;
		}
	}
	
	
	// Number of {combination of activity and attribute} per person
	// @param activity: {TYPE OF DISCHARGE, ACTIVITY ATTRIBUTE}
	// @param attribute: {MTI, CT Scan, Ultrasound Scan} {Left against medical advice, Left before completion of treatment, Voluntary left without notification to ED operators}
	public MeasureDefinition buildNoOfActivityAttributes(String activity, String attribute, boolean privatize, double epsilon) {
		if (privatize) {
			DataMeasure activityAttribute = new DataMeasure();
			activityAttribute.setDataContentSelection(new DataContentSelection(activity, attribute));
			
			//privatize sum using laplacem echanism and minimum and maximum element in timeframe as boundaries
			PrivacyAwareAggregatedMeasure activityAttributesSum = new PrivacyAwareAggregatedMeasure();
			activityAttributesSum.setBaseMeasure(activityAttribute);
			activityAttributesSum.setAggregationFunction(PrivacyAwareAggregator.SUM_LAP);
			activityAttributesSum.setEpsilon(epsilon);
			activityAttributesSum.setBoundaryEstimation(BoundaryEstimator.MINMAX);
			
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

			// divide number of activity attributes by number of process instances			
			DerivedMultiInstanceMeasure xPerPerson = new DerivedMultiInstanceMeasure();
			xPerPerson.setFunction("a/b");
			xPerPerson.addUsedMeasure("a", activityAttributesSum);
			xPerPerson.addUsedMeasure("b", totalSum);
			
			return xPerPerson;
		}
		else {
			DataMeasure activityAttribute = new DataMeasure();
			activityAttribute.setDataContentSelection(new DataContentSelection(activity, attribute));
			
			AggregatedMeasure activityAttributesSum = new AggregatedMeasure();
			activityAttributesSum.setBaseMeasure(activityAttribute);
			activityAttributesSum.setAggregationFunction(Aggregator.SUM);
			
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
			
			// divide number of activity attributes by number of process instances
			DerivedMultiInstanceMeasure xPerPerson = new DerivedMultiInstanceMeasure();
			xPerPerson.setFunction("a/b");
			xPerPerson.addUsedMeasure("a", activityAttributesSum);
			xPerPerson.addUsedMeasure("b", totalSum);
			
			return xPerPerson;
		}
	}
	
	
	
	// Length of stay for discharged patients
	public MeasureDefinition buildLenOfStayDischarged(boolean privatize, double epsilon) {
		if (privatize) {
			//get time from triage (i.e. beginning of each trace to discharge event)
			TimeMeasure t = new TimeMeasure();
			t.setFrom(new TimeInstantCondition("TRIAGE", GenericState.END));
			t.setTo(new TimeInstantCondition("DISCHARGE", GenericState.END));
			t.setUnitOfMeasure(TimeUnit.MINUTES);
	
			//get mean of all times using laplace mechanism on mean and boundary as min and max values of time frame
			PrivacyAwareAggregatedMeasure mean = new PrivacyAwareAggregatedMeasure();
			mean.setBaseMeasure(t);
			mean.setAggregationFunction(PrivacyAwareAggregator.AVG_LAP);
			mean.setEpsilon(epsilon);
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
		
	
	// radiological exam durations - RADIOLOGICAL EXAM REQUEST -> RADIOLOGICAL EXAM REPORT
	public MeasureDefinition buildRadiologyDuration(boolean privatize, double epsilon) {
		if (privatize) {
			TimeMeasure t = new TimeMeasure();
			t.setFrom(new TimeInstantCondition("RADIOLOGICAL EXAM REQUEST", GenericState.END));
			t.setTo(new TimeInstantCondition("RADIOLOGICAL EXAM REPORT", GenericState.END));
			t.setUnitOfMeasure(TimeUnit.MINUTES);
	
			//get mean of all times using laplace mechanism on mean and boundary as min and max values of time frame
			PrivacyAwareAggregatedMeasure mean = new PrivacyAwareAggregatedMeasure();
			mean.setBaseMeasure(t);
			mean.setAggregationFunction(PrivacyAwareAggregator.AVG_LAP);
			mean.setEpsilon(epsilon);
			mean.setBoundaryEstimation(BoundaryEstimator.MINMAX);
			
			return mean;
		}
		else {
			TimeMeasure t = new TimeMeasure();
			t.setFrom(new TimeInstantCondition("RADIOLOGICAL EXAM REQUEST", GenericState.END));
			t.setTo(new TimeInstantCondition("RADIOLOGICAL EXAM REPORT", GenericState.END));
			t.setUnitOfMeasure(TimeUnit.MINUTES);
	
			//get mean of all times
			AggregatedMeasure mean = new AggregatedMeasure();
			mean.setBaseMeasure(t);
			mean.setAggregationFunction(Aggregator.AVG);
			
			return mean;
		}
	}
	
	
	// lab report durations - LAB REQUEST -> LAB REPORT
	public MeasureDefinition buildLabDuration(boolean privatize, double epsilon) {
		if (privatize) {
			TimeMeasure t = new TimeMeasure();
			t.setFrom(new TimeInstantCondition("LAB REQUEST", GenericState.END));
			t.setTo(new TimeInstantCondition("LAB REPORT", GenericState.END));
			t.setUnitOfMeasure(TimeUnit.MINUTES);
	
			//get mean of all times using laplace mechanism on mean and boundary as min and max values of time frame
			PrivacyAwareAggregatedMeasure mean = new PrivacyAwareAggregatedMeasure();
			mean.setBaseMeasure(t);
			mean.setAggregationFunction(PrivacyAwareAggregator.AVG_LAP);
			mean.setEpsilon(epsilon);
			mean.setBoundaryEstimation(BoundaryEstimator.MINMAX);
			
			return mean;
		}
		else {
			TimeMeasure t = new TimeMeasure();
			t.setFrom(new TimeInstantCondition("LAB REQUEST", GenericState.END));
			t.setTo(new TimeInstantCondition("LAB REPORT", GenericState.END));
			t.setUnitOfMeasure(TimeUnit.MINUTES);
	
			//get mean of all times
			AggregatedMeasure mean = new AggregatedMeasure();
			mean.setBaseMeasure(t);
			mean.setAggregationFunction(Aggregator.AVG);
			
			return mean;
		}
	}


}
