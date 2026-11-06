package ma.rest.benchmark.controllers.jersey;

import ma.rest.benchmark.dto.ItemDTO;
import ma.rest.benchmark.service.CatalogService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JerseyItemResource {

    private final CatalogService catalogService;

    public JerseyItemResource(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GET
    public Response getItems(
            @QueryParam("categoryId") Long categoryId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        if (categoryId != null) {
            Page<ItemDTO> items = catalogService.getItemsByCategory(categoryId, page, size);
            return Response.ok(items.getContent()).build();
        }
        Page<ItemDTO> items = catalogService.getItems(page, size);
        return Response.ok(items.getContent()).build();
    }

    @GET
    @Path("/{id}")
    public Response getItemById(@PathParam("id") Long id) {
        try {
            ItemDTO item = catalogService.getItemById(id);
            return Response.ok(item).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response createItem(ItemDTO dto) {
        try {
            ItemDTO created = catalogService.createItem(dto);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateItem(@PathParam("id") Long id, ItemDTO dto) {
        try {
            ItemDTO updated = catalogService.updateItem(id, dto);
            return Response.ok(updated).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteItem(@PathParam("id") Long id) {
        catalogService.deleteItem(id);
        return Response.noContent().build();
    }
}
