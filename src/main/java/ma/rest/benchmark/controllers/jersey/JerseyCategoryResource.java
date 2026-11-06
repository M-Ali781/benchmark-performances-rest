package ma.rest.benchmark.controllers.jersey;

import ma.rest.benchmark.dto.CategoryDTO;
import ma.rest.benchmark.dto.ItemDTO;
import ma.rest.benchmark.service.CatalogService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Component
@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JerseyCategoryResource {

    private final CatalogService catalogService;

    public JerseyCategoryResource(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GET
    public Response getCategories(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        Page<CategoryDTO> categories = catalogService.getCategories(page, size);
        return Response.ok(categories.getContent()).build();
    }

    @GET
    @Path("/{id}")
    public Response getCategoryById(@PathParam("id") Long id) {
        try {
            CategoryDTO dto = catalogService.getCategoryById(id);
            return Response.ok(dto).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{id}/items")
    public Response getCategoryItems(@PathParam("id") Long id) {
        List<ItemDTO> items = catalogService.getCategoryItems(id);
        return Response.ok(items).build();
    }

    @POST
    public Response createCategory(CategoryDTO dto) {
        CategoryDTO created = catalogService.createCategory(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateCategory(@PathParam("id") Long id, CategoryDTO dto) {
        try {
            CategoryDTO updated = catalogService.updateCategory(id, dto);
            return Response.ok(updated).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") Long id) {
        catalogService.deleteCategory(id);
        return Response.noContent().build();
    }
}
