package com.projects;


import com.database.MongoUtil;
import com.database.SortingOrder;
import com.endpoints.resources.ProjectResource;
import com.projects.tasks.Task;
import com.projects.tasks.TasksCollection;
import com.projects.tasks.submits.Submit;
import com.projects.tasks.submits.SubmitsCollection;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class ProjectService {
    private static ProjectService instance; // Singleton instance
    private MongoUtil mongoUtil;

    @Getter
    private ProjectCollection projectCollection;
    @Getter
    private TasksCollection tasksCollection;
    @Getter
    private SubmitsCollection submitsCollection;

    // Private constructor to prevent direct instantiation
    private ProjectService() {
        mongoUtil = new MongoUtil();
        mongoUtil.connect();
        projectCollection = new ProjectCollection(mongoUtil);
        tasksCollection = new TasksCollection(mongoUtil);
        submitsCollection = new SubmitsCollection(mongoUtil);
    }

    // Public method to provide access to the singleton instance
    public static synchronized ProjectService getInstance() {
        if (instance == null) {
            instance = new ProjectService();
        }
        return instance;
    }

    public void createProject(ProjectResource.ProjectCreationInfo projectCreationInfo,
                              String creatorAddress) {
        Project project = new Project(projectCreationInfo);


        String projectAddress = projectCreationInfo.getProjectAddress();
        List<RoleInProject> roles =
                new java.util.ArrayList<>(Arrays.stream(projectCreationInfo.getConfirmers())
                        .map(confirmer -> new RoleInProject(
                                confirmer,
                                Role.CONFIRMER_ROLE,
                                projectAddress))
                        .toList());
        RoleInProject creatorRole = new RoleInProject(creatorAddress,
                Role.DEFAULT_ADMIN_ROLE,
                projectAddress);
        roles.add(creatorRole);

        project.setAccountRoles(roles);

        project.setCreatorAddress(creatorAddress);

        projectCollection.save(project);
    }

    public Project get(String projectAddress) {
        return projectCollection.get(projectAddress);
    }

    public List<Project> getProjectsOfAccount(String wallet) {
        return projectCollection.getAllProjectsOfAddress(wallet);
    }

    public List<String> getAddressesOfProjectsByAccount(String wallet) {
        return projectCollection.getAllProjectsOfAddress(wallet)
                .stream()
                .map(Project::getProjectAddress)
                .toList();
    }

    public void createTask(Project project, Task task) {
        project.addTask(task.getId());
        projectCollection.update(project);
        tasksCollection.save(task);
    }

    public void createSubmit(Task task, Submit submit) {
        task.addSubmit(submit.getId());
        tasksCollection.update(task);
        submitsCollection.save(submit);
    }

    public Submit getSubmit(String submitAddress) {
        return submitsCollection.get(submitAddress);
    }

    public List<Submit> getSubmits(List<String> submitAddresses) {
        return submitsCollection.get(submitAddresses);
    }
    public Task getTask(String taskAddress) {
        return tasksCollection.get(taskAddress);
    }

    public List<Task> getTasks(String[] taskAddresses) {
        return tasksCollection.getAll(taskAddresses);
    }

    public void updateTask(Task task) {
        tasksCollection.update(task);
    }

    public void updateSubmit(Submit submit) {
        submitsCollection.update(submit);
    }
}
