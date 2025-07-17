package integration

import interfaces.DataCollector
import interfaces.CommitFilter
import interfaces.ProjectProcessor
import interfaces.OutputProcessor
import services.commitFilters.InCommitListMutuallyModifiedMethodsFilter
import services.dataCollectors.StatisticsCollector
import util.*
import services.dataCollectors.modifiedLinesCollector.ModifiedLinesCollector

import com.google.inject.*
import com.google.inject.multibindings.Multibinder

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<DataCollector> dataCollectorBinder = Multibinder.newSetBinder(binder(), DataCollector.class);

        dataCollectorBinder.addBinding().to(ModifiedLinesCollector.class);
        dataCollectorBinder.addBinding().to(StatisticsCollector.class);
        bind(CommitFilter.class).to(InCommitListMutuallyModifiedMethodsFilter.class)

        Multibinder<ProjectProcessor> projectProcessorBinder = Multibinder.newSetBinder(binder(), ProjectProcessor.class)
        projectProcessorBinder.addBinding().to(EmptyProjectProcessor.class)

        Multibinder<OutputProcessor> outputProcessorBinder = Multibinder.newSetBinder(binder(), OutputProcessor.class)
        outputProcessorBinder.addBinding().to(EmptyOutputProcessor.class)
    }

}