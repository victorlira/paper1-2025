package services.commitFilters

import app.MiningFramework
import interfaces.CommitFilter
import project.MergeCommit
import project.Project
import static app.MiningFramework.arguments


/**
 *  Combines the output of IsInCommitListFilter, MutuallyModifiedMethodsCommitFilter
 *  and TextualConflictFilter with and clauses
 */
class InCommitListMutuallyModifiedMethodsTextualConflictCommitFilter implements CommitFilter {
    private CommitFilter commitListFilter = new IsInCommitListFilter();
    private CommitFilter mutuallyModifiedFilter = new MutuallyModifiedMethodsCommitFilter();
    private CommitFilter textualConflictFilter = new TextualConflictFilter();

    private File filteredScenarios = null;

    void createLogFileIfDoesntExist() {
        File dataFolder = new File(arguments.getOutputPath() + "/data/");
        filteredScenarios = new File(dataFolder.getAbsolutePath() + "/filtered_scenarios.csv");

        if (!filteredScenarios.exists()) {
            dataFolder.mkdirs()
            filteredScenarios << "project,merge commit,filter reason\n"
        }
    }

    void addFilteredLog(Project project, MergeCommit mergeCommit, String reason) {
        filteredScenarios << project.getName() + "," + mergeCommit.getSHA() + "," + reason + "\n"
    }


    @Override
    boolean applyFilter(Project project, MergeCommit mergeCommit) {
        createLogFileIfDoesntExist();
        boolean isInCommitList = commitListFilter.applyFilter(project, mergeCommit);
        if (!isInCommitList) {
            return false;
        }

        boolean hasMutuallyModifiedMethods = mutuallyModifiedFilter.applyFilter(project, mergeCommit);
        if (!hasMutuallyModifiedMethods) {
            addFilteredLog(project, mergeCommit, "no mutually modified methods")
            return false;
        }

        boolean passMergeConflictFilter = textualConflictFilter.applyFilter(project, mergeCommit)
        if (!passMergeConflictFilter) {
            addFilteredLog(project, mergeCommit, "textual merge conflict")
            return false;
        }

        return true;
    }
}
