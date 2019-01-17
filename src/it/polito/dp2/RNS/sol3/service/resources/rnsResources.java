package it.polito.dp2.RNS.sol3.service.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.dp2.RNS.sol3.rest.service.jaxb.*;
import it.polito.dp2.RNS.sol3.service.service.SearchVehicles;
import it.polito.dp2.RNS.sol3.service.service.rnsService;
import it.polito.dp2.RNS.sol3.service.service.SearchPlaces;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

/**
 * Copyright by Jacopx on 2019-01-16.
 */
@Path("/")
@Api(value = "/")
public class rnsResources {
    public UriInfo uriInfo;

    rnsService service = new rnsService();

    public rnsResources(@Context UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @GET
    @ApiOperation(value = "getRnsSystem", notes = "reads main resource")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
    })
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public RnsSystem getRnsSystem() {
        RnsSystem rns = new RnsSystem();
        UriBuilder root = uriInfo.getAbsolutePathBuilder();
        UriBuilder places = root.clone().path("places");
        rns.setSelf(root.toTemplate());
        rns.setPlaces(places.toTemplate());
        rns.setVehicles(root.clone().path("vehicles").toTemplate());
        rns.setConnections(root.clone().path("connections").toTemplate());
        return rns;
    }

    @GET
    @Path("/places")
    @ApiOperation(value = "getPlaces", notes = "searches places"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
    })
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Places getPlaces(@QueryParam("keyword") String keyword,
                           @QueryParam("type") String type
    ) {
        return service.getPlaces(SearchPlaces.ALL, keyword, type);
    }

    @POST
    @Path("/places")
    @ApiOperation(value = "createPlace", notes = "create a new place"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
    })
    @Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Response createPlace(Place place) {
        long id = service.getNextId();
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(id));
        URI self = builder.build();
        place.setSelf(self.toString());
        URI connections = builder.clone().path("connections").build();
        place.setConnections(connections.toString());
        URI connectedBy = builder.clone().path("connectedBy").build();
        place.setConnectedBy(connectedBy.toString());

        Place created = service.createPlace(id, place);
        if (created!=null) {
            return Response.created(self).entity(created).build();
        } else
            throw new InternalServerErrorException();
    }

    @GET
    @Path("/places/{id}")
    @ApiOperation(value = "getPlace", notes = "read single place"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
    })
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Place getItem(@PathParam("id") long id) {
        Place place = service.getPlace(id);
        if (place==null)
            throw new NotFoundException();
        return place;
    }

    @PUT
    @Path("/places/{id}")
    @ApiOperation(value = "updatePlace", notes = "update single place"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
    })
    @Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Place updatePlace(@PathParam("id") long id, Place place) {
        Place updated = service.updatePlace(id, place);
        if (updated==null)
            throw new NotFoundException();
        return updated;
    }

    @DELETE
    @Path("/places/{id}")
    @ApiOperation(value = "deleteItem", notes = "delete single item"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 409, message = "Conflict (item is cited)"),
    })
    public void deleteItem(@PathParam("id") long id) {
        Place place = service.deletePlace(id);
        if (place==null)
            throw new NotFoundException();
        return;
    }

    @GET
    @Path("/connections")
    @ApiOperation(value = "getConnections", notes = "searches connections"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
    })
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Connections getConnections() {
        return service.getConnections();
    }

    @GET
    @Path("/connections/{id}")
    @ApiOperation(value = "getConnection", notes = "read single connection"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
    })
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Connection getConnection(@PathParam("id") String id) {
        Connection connection = service.getConnection(id);
        if (connection==null)
            throw new NotFoundException();
        return connection;
    }

    @GET
    @Path("/vehicles")
    @ApiOperation(value = "getVehicles", notes = "searches vehicles"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
    })
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Vehicles getVehicles(@QueryParam("keyword") String keyword,
                                @QueryParam("type") String type,
                                @QueryParam("state") String state,
                                @QueryParam("entrytime") String entrytime,
                                @QueryParam("position") String position
    ) {
        return service.getVehicles(SearchVehicles.ALL, keyword, type, state, entrytime, position);
    }

    @GET
    @Path("/vehicles/{id}")
    @ApiOperation(value = "getVehicle", notes = "read single vehicle"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
    })
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Vehicle getVehicle(@PathParam("id") String id) {
        Vehicle vehicle = service.getVehicle(id);
        if (vehicle==null)
            throw new NotFoundException();
        return vehicle;
    }

}
