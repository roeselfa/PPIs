package pappi;

import es.us.isa.ppinot.evaluation.computers.AggregatedMeasureComputer;
import es.us.isa.ppinot.evaluation.computers.CountMeasureComputer;
import es.us.isa.ppinot.evaluation.computers.DataMeasureComputer;
import es.us.isa.ppinot.evaluation.computers.DerivedMeasureComputer;
import es.us.isa.ppinot.evaluation.computers.MeasureComputer;
import es.us.isa.ppinot.evaluation.computers.MeasureComputerFactory;
import es.us.isa.ppinot.evaluation.computers.StateConditionMeasureComputer;
import es.us.isa.ppinot.evaluation.computers.TimeMeasureComputer;
import es.us.isa.ppinot.model.MeasureDefinition;
import es.us.isa.ppinot.model.ProcessInstanceFilter;
import es.us.isa.ppinot.model.aggregated.AggregatedMeasure;
import es.us.isa.ppinot.model.base.CountMeasure;
import es.us.isa.ppinot.model.base.DataMeasure;
import es.us.isa.ppinot.model.base.StateConditionMeasure;
import es.us.isa.ppinot.model.base.TimeMeasure;
import es.us.isa.ppinot.model.derived.DerivedMeasure;

public class PrivacyAwareMeasureComputerFactory extends MeasureComputerFactory{
    public MeasureComputer create(MeasureDefinition definition, ProcessInstanceFilter filter) {
        MeasureComputer computer = null;

        //base measures are kept as they are
        if (definition instanceof TimeMeasure) {
            computer = new TimeMeasureComputer(definition, filter);
        } else if (definition instanceof CountMeasure) {
            computer = new CountMeasureComputer(definition);
        } else if (definition instanceof StateConditionMeasure) {
            computer = new StateConditionMeasureComputer(definition);
        } else if (definition instanceof DataMeasure) {
            computer = new DataMeasureComputer(definition);
        
        } else if (definition instanceof PrivacyAwareAggregatedMeasure) {
        	computer = new PrivacyAwareAggregatedMeasureComputer(definition, filter);
            
        } else if (definition instanceof AggregatedMeasure) {
            computer = new AggregatedMeasureComputer(definition, filter);
      
        
        //TODO PrivacyAwareDerivedMeasureComputer
        } else if (definition instanceof DerivedMeasure) {
            computer = new PrivacyAwareDerivedMeasureComputer(definition, filter);
        }

        return computer;

    }
}
