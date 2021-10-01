package applications;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;

import es.us.isa.ppinot.evaluation.Measure;
import es.us.isa.ppinot.evaluation.evaluators.LogMeasureEvaluator;
import es.us.isa.ppinot.evaluation.evaluators.MeasureEvaluator;
import es.us.isa.ppinot.evaluation.logs.LogProvider;
import es.us.isa.ppinot.evaluation.logs.MXMLLog;
import es.us.isa.ppinot.model.MeasureDefinition;
import es.us.isa.ppinot.model.scope.Period;
import es.us.isa.ppinot.model.scope.SimpleTimeFilter;
import pappi.PrivacyAwareLogMeasureEvaluator;

public class CaseStudyPpi {

	public static void main(String[] args) throws Exception {
		//after each PPI log need to be loaded again, otherwise result set will be empty
		MXMLLog case_log = new MXMLLog(new FileInputStream(new File(String.join(File.separator,"logs","All_Severity_codes.mxml"))),null);
		
		boolean privatized = true;
		List<Measure> measures;
		
		//PPI 1
		//measures = computePPI(case_log, CaseStudyMeasureDefinitions.buildPPI1(!privatized),!privatized);
		//System.out.println("PPI 1 Not privatized: "+measures);
		
		//case_log = new MXMLLog(new FileInputStream(new File(String.join(File.separator,"logs","All_Severity_codes.mxml"))),null);
		//measures = computePPI(case_log, CaseStudyMeasureDefinitions.buildPPI1(privatized),privatized);
		//System.out.println("PPI 1 privatized    : "+measures);
		
		//PPI 2
		case_log = new MXMLLog(new FileInputStream(new File(String.join(File.separator,"logs","All_Severity_codes.mxml"))),null);
		measures = computePPI(case_log, CaseStudyMeasureDefinitions.buildPPI2(!privatized),!privatized);
		System.out.println("PPI 2 Not privatized: "+measures);
		
		case_log = new MXMLLog(new FileInputStream(new File(String.join(File.separator,"logs","All_Severity_codes.mxml"))),null);
		measures = computePPI(case_log, CaseStudyMeasureDefinitions.buildPPI2(privatized),privatized);
		System.out.println("PPI 2 privatized    : "+measures);

		//PPI 3
		case_log = new MXMLLog(new FileInputStream(new File(String.join(File.separator,"logs","All_Severity_codes.mxml"))),null);
		measures = computePPI(case_log, CaseStudyMeasureDefinitions.buildPPI3(!privatized),!privatized);
		System.out.println("PPI 3 Not privatized: "+measures);
		case_log = new MXMLLog(new FileInputStream(new File(String.join(File.separator,"logs","All_Severity_codes.mxml"))),null);
		measures = computePPI(case_log, CaseStudyMeasureDefinitions.buildPPI3(privatized),privatized);
		System.out.println("PPI 3 privatized    : "+measures);
	}
	
	
	static List<Measure> computePPI(LogProvider log, MeasureDefinition measure, boolean privatized) throws Exception {
		LogMeasureEvaluator evaluator;
		if (privatized) {
			evaluator = new PrivacyAwareLogMeasureEvaluator(log);	
		}
		else {
			evaluator = new LogMeasureEvaluator(log);
		}	
		return evaluator.eval(measure, new SimpleTimeFilter(Period.MONTHLY,4, false));
	}

	
}
