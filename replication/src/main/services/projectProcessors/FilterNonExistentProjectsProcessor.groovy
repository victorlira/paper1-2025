package services.projectProcessors

import exception.GithubHelperException
import interfaces.ProjectProcessor
import project.Project
import util.GithubHelper

import java.util.stream.Collectors

import static app.MiningFramework.arguments

/**
 * @provides: filters off the projects are not accessible on github
 */
class FilterNonExistentProjectsProcessor implements ProjectProcessor {

    @Override
    ArrayList<Project> processProjects(ArrayList<Project> projects) {
        GithubHelper githubHelper = new GithubHelper(arguments.getAccessKey())

        println "Running FilterNonExistentProjectsProcessor"
        return projects.stream()
                .filter(project -> {
                    def projectRepository = githubHelper.getRepository(project)

                    if (projectRepository == null)
                        println "Project ${project.getName()} doesn't exist or is not acessible: skiping it"

                    return projectRepository != null
                })
                .collect(Collectors.toList())
    }
}
