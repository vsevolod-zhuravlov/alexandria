package com.projects;

import com.accounts.Account;
import com.endpoints.resources.ProjectResource;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Project {
    public String projectAddress;

    public String name;
    public String description;
    public String fullDescription;
    public Date deadline;
    public boolean isPublic;

    private String creatorAddress;
    private List<RoleInProject> accountRoles;
    private List<String> tasks;

    private String imageUrl;

    public Project(ProjectResource.ProjectCreationInfo projectCreationInfo) {
        this.projectAddress = projectCreationInfo.getProjectAddress();
        this.name = projectCreationInfo.getName();
        this.description = projectCreationInfo.getDescription();
        this.fullDescription = projectCreationInfo.getFullDescription();
        this.deadline = projectCreationInfo.getDeadline();
        this.isPublic = projectCreationInfo.isPublic();
    }

    public void addTask(String task) {
        this.tasks.add(task);
    }

    public boolean walletHasRole(Role role, String walletAddress) {
        for (RoleInProject roleInProject : accountRoles) {
            if (roleInProject.role.equals(role)) {
                if (roleInProject.walletNumber.equals(walletAddress)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addAccount(String address, Role role) {
        RoleInProject roleInProject = new RoleInProject(
                address,
                role,
                this.projectAddress
        );
        accountRoles.add(roleInProject);
    }

    public Role getRole(String address) {
        for (RoleInProject roleInProject : accountRoles) {
            if (roleInProject.walletNumber.equals(address)) {
                return roleInProject.role;
            }
        }
        return null;
    }
}
