package com.endpoints.resources;

import com.endpoints.AppSecretKey;
import com.projects.Project;
import com.projects.ProjectService;
import com.projects.Role;
import com.projects.tasks.Task;
import com.projects.tasks.submits.Confirmation;
import com.projects.tasks.submits.Submit;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Path("/project")
public class ProjectResource {
    private static final ProjectService projectService = ProjectService.getInstance();
    private static final SecretKey key = AppSecretKey.getSecretKey();

    //private static final AccountsCollection accountsCollection = AccountsService.getInstance().getAccountsCollection();

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProject(String projectAddress,
                               @HeaderParam("Authorization") String authHeader) throws Exception {

        Project project = projectService.get(projectAddress);
        if (project != null) {
            if (project.isPublic()) {
                return Response.ok(project).build();
            } else {
                boolean authorised = hasConfirmerRights(project, authHeader);
                return authorised
                        ? Response.ok(project).build()
                        : Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/join")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response joinProject(String projectAddress,
                                @HeaderParam("Authorization") String authHeader) throws Exception {
        Project project = projectService.get(projectAddress);
        if (project == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!project.isPublic()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String walletAddress = getWalletAddress(authHeader);
        project.addAccount(walletAddress, Role.STUDENT_ROLE);
        return Response.ok(project).build();
    }


    @GET
    @Path("/my-projects")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getMyProjects(@HeaderParam("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer:")) {
            return null;
        }
        try {
            String walletAddress = getWalletAddress(authHeader);
            return projectService.getAddressesOfProjectsByAccount(walletAddress);
        } catch (
                Exception e) {
            return null;
        }
    }

    @GET
    @Path("/getAllTasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTasks(
            @QueryParam("projectAddress") String projectAddress,
            @HeaderParam("Authorization") String authHeader) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer:"))
            return Response.status(Response.Status.UNAUTHORIZED).build();

        Project project = projectService.get(projectAddress);

        if (project == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        String walletAddress = getWalletAddress(authHeader);

        Role role = project.getRole(walletAddress);

        if (role == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();

        String[] taskAddresses = project.getTasks().toArray(new String[0]);

        List<Task> tasks = projectService.getTasks(taskAddresses);

        return Response.ok(tasks).build();

    }

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTask(
            @QueryParam("projectAddress") String projectAddress,
            @QueryParam("taskId") String taskId,
            @HeaderParam("Authorization") String authHeader) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer:"))
            return Response.status(Response.Status.UNAUTHORIZED).build();

        Project project = projectService.get(projectAddress);

        if (project == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        String walletAddress = getWalletAddress(authHeader);

        Role role = project.getRole(walletAddress);

        if (role == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();

        if (project.getTasks().contains(taskId)) {

            projectService.getTask(taskId);

            return Response.ok(project.getTasks()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/check")
    public Response checkTask(@QueryParam("projectAddress") String projectAddress,
                              @QueryParam("taskAddress") String taskAddress,
                              @QueryParam("submitAddress") String submitAddress,
                              @QueryParam("accepted") boolean accepted,
                              @HeaderParam("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer:")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            Project project = projectService.get(projectAddress);
            if (hasConfirmerRights(project, authHeader)) {
                String walletAddress = getWalletAddress(authHeader);
                Task task = projectService.getTask(taskAddress);
                Submit submit = projectService.getSubmit(submitAddress);
                if (submit != null) {
                    int minSubmits = task.getMinConfirmations();
                    Confirmation confirmation = accepted
                            ? new Confirmation(walletAddress, Confirmation.Status.CONFIRMED)
                            : new Confirmation(walletAddress, Confirmation.Status.DECLINED);

                    submit.addConfirmation(minSubmits, confirmation);
                    projectService.updateSubmit(submit);
                    return Response.status(Response.Status.OK).build();
                }
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }


        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProject(ProjectCreationInfo projectInfo,
                                  @HeaderParam("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer:")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            String walletAddress = getWalletAddress(authHeader);

            projectService.createProject(projectInfo, walletAddress);

            return Response.ok("Project created by: " + walletAddress).build();
        } catch (
                Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Authorisation data is in wrong format.")
                    .build();
        }

    }

    @POST
    @Path("/create-task")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTask(Task task,
                               @QueryParam("projectAddress") String projectAddress,
                               @HeaderParam("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer:")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try {
            String walletAddress = getWalletAddress(authHeader);

            Project project = projectService.get(projectAddress);

            if (project.walletHasRole(Role.DEFAULT_ADMIN_ROLE, walletAddress)) {
                projectService.createTask(project, task);
            }
            return Response.ok("Task created by: " + walletAddress).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @POST
    @Path("/submit")
    public Response submit(Submit submit,
                           @QueryParam("projectAddress") String projectAddress,
                           @QueryParam("taskId") String taskId,
                           @HeaderParam("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer:")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            Project project = projectService.get(projectAddress);
            if (hasStudentRights(project, authHeader)) {
                Task task = projectService.getTask(taskId);
                if (task != null) {
                    projectService.createSubmit(task, submit);
                }
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }


        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

    }


    /*
     * {
     * "projectAddress":"string",
     * "name":"string",
     * "description":"string",
     * "fullDescription":"string",
     * "deadline": timestamp,
     * confirmers: ["string", "string", "string"],
     * isPublic: "boolean"
     * }
     *
     * */
    @AllArgsConstructor
    @Getter
    public static class ProjectCreationInfo {
        private final String projectAddress;
        private final String name;
        private final String description;
        private final String fullDescription;
        private final Date deadline;
        private final String[] confirmers;
        private final boolean isPublic;
    }

    private static String getTokenText(String authHeader) {
        return authHeader
                .substring("Bearer:{\"token\": \"".length(), authHeader.length() - 2)
                .trim();
    }

    private static String getWalletAddress(String authHeader) throws Exception {
        String tokenString = getTokenText(authHeader);
        // Decode JWT and get the address
        Claims claims = Jwts.parser()
                .verifyWith(key)  // Verify signature
                .build()
                .parseSignedClaims(tokenString)  // Parse signed JWT
                .getPayload();  // Extract claims

        // Extract wallet address from "sub" (subject)
        return claims.getSubject();
    }

    private boolean hasConfirmerRights(Project project, String authHeader) throws Exception {
        String tokenString = getTokenText(authHeader);

        String walletAddress = getWalletAddress(tokenString);

        return project.walletHasRole(Role.DEFAULT_ADMIN_ROLE, walletAddress)
                || project.walletHasRole(Role.CONFIRMER_ROLE, walletAddress);
    }

    private boolean hasStudentRights(Project project, String authHeader) throws Exception {
        String tokenString = getTokenText(authHeader);

        String walletAddress = getWalletAddress(tokenString);

        return project.walletHasRole(Role.STUDENT_ROLE, walletAddress);
    }

}
