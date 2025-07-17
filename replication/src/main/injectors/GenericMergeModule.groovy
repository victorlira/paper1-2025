package injectors

import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder
import interfaces.CommitFilter
import interfaces.DataCollector
import interfaces.OutputProcessor
import interfaces.ProjectProcessor
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import services.commitFilters.MutuallyModifiedFilesCommitFilter
import services.dataCollectors.GenericMerge.GenericMergeConfig
import services.dataCollectors.GenericMerge.GenericMergeDataCollector
import services.dataCollectors.GenericMerge.MergeConflictsComparator
import services.dataCollectors.GenericMerge.MergeToolsComparator
import services.dataCollectors.GenericMerge.ScenarioLOCsCounter
import services.dataCollectors.GenericMerge.UnstructuredMergeCollector
import services.outputProcessors.GenericMergeDataOutputProcessor
import services.projectProcessors.DummyProjectProcessor
import services.util.ci.CIPlatform
import services.util.ci.TravisPlatform

import java.nio.file.Files
import java.nio.file.Paths

class GenericMergeModule extends AbstractModule {
    private static Logger LOG = LogManager.getLogger(GenericMergeModule.class)

    @Override
    protected void configure() {
        Multibinder<ProjectProcessor> projectProcessorBinder = Multibinder.newSetBinder(binder(), ProjectProcessor.class)
        projectProcessorBinder.addBinding().to(DummyProjectProcessor.class)

        Multibinder<DataCollector> dataCollectorBinder = Multibinder.newSetBinder(binder(), DataCollector.class)
        dataCollectorBinder.addBinding().to(ScenarioLOCsCounter.class)
        dataCollectorBinder.addBinding().to(GenericMergeDataCollector.class)
        dataCollectorBinder.addBinding().to(MergeToolsComparator.class)
        dataCollectorBinder.addBinding().to(MergeConflictsComparator.class)
        dataCollectorBinder.addBinding().to(UnstructuredMergeCollector.class)

        Multibinder<OutputProcessor> outputProcessorBinder = Multibinder.newSetBinder(binder(), OutputProcessor.class)
        outputProcessorBinder.addBinding().to(GenericMergeDataOutputProcessor.class)

        bind(CommitFilter.class).to(MutuallyModifiedFilesCommitFilter.class)
        bind(CIPlatform.class).to(TravisPlatform.class)

        createExecutionReportsFile()
    }

    private static void createExecutionReportsFile() {
        LOG.info("Creating Generic Merge report file")
        Files.createDirectories(Paths.get(GenericMergeConfig.GENERIC_MERGE_REPORT_PATH))
        def reportFile = new File(GenericMergeConfig.GENERIC_MERGE_REPORT_FILE_NAME)
        reportFile.createNewFile()
    }
}
