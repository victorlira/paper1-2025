package services.commitFilters

import interfaces.CommitFilter
import project.*
import services.dataCollectors.modifiedLinesCollector.ModifiedMethod
import util.*
import services.dataCollectors.modifiedLinesCollector.ModifiedMethodsHelper

import java.util.stream.Collectors

/**
 * @requires: that a diffj cli is in the dependencies folder
 * @provides: returns true if both left and right of the merge scenario have a intersection on the modified methods list
 */
class MutuallyModifiedMethodsCommitFilter implements CommitFilter {

    private static final Object lock = new Object()

    private modifiedMethodsHelper = new ModifiedMethodsHelper("diffj.jar");
    static i = 0
    boolean applyFilter(Project project, MergeCommit mergeCommit) {
        return containsMutuallyModifiedMethods(project, mergeCommit)
    }

    private boolean containsMutuallyModifiedMethods(Project project, MergeCommit mergeCommit) {
        if (mergeCommit.getAncestorSHA() == null) {
            /**
            * Some merge scenarios don't return an valid ancestor SHA this check prevents
            * unexpected crashes
            */
            return false;
        } 

        Set<String> leftModifiedFiles = FileManager.getModifiedFiles(project, mergeCommit.getLeftSHA(), mergeCommit.getAncestorSHA())
        Set<String> rightModifiedFiles = FileManager.getModifiedFiles(project, mergeCommit.getRightSHA(), mergeCommit.getAncestorSHA())
        Set<String> mutuallyModifiedFiles = new HashSet<String>(leftModifiedFiles)
        mutuallyModifiedFiles.retainAll(rightModifiedFiles)

        for(file in mutuallyModifiedFiles) {
            Set<String> leftModifiedAttributesAndMethods = getModifiedAttributesAndMethods(project, file, mergeCommit.getAncestorSHA(), mergeCommit.getLeftSHA())
            Set<String> rightModifiedAttributesAndMethods = getModifiedAttributesAndMethods(project, file, mergeCommit.getAncestorSHA(), mergeCommit.getRightSHA())

            leftModifiedAttributesAndMethods.retainAll(rightModifiedAttributesAndMethods) // Intersection.

            if(leftModifiedAttributesAndMethods.size() > 0) {
                synchronized (lock) {
                    new FileWriter("result-victor.txt", true).with {
                        write(project.getName() + ': ' + mergeCommit.getSHA() + '\n')
                        flush()
                        close()
                    }
                }
                i = i + 1
                println '-----------------------'
                println 'SCENÁRIOS '
                println i
                println project.getName() + ': ' + mergeCommit.getSHA()
                println '-----------------------'
                return true
            }
        }

        return false
    }

    private Set<String> getModifiedAttributesAndMethods(Project project, String filePath, String ancestorSHA, String targetSHA) {
        return modifiedMethodsHelper.getModifiedMethods(project, filePath, ancestorSHA, targetSHA)
                .stream()
                .map(method -> method.getSignature())
                .collect(Collectors.toSet())
    }

}