package org.jahia.modules.tags.actions;

import org.apache.commons.collections.CollectionUtils;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.tags.BaseTagAction;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Add tag(s) action
 *
 * @author kevan
 */
public class AddTag extends BaseTagAction{

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        JCRSessionWrapper jcrSessionWrapper = resource.getNode().getSession();
        JCRNodeWrapper node = resource.getNode();

        if(CollectionUtils.isNotEmpty(parameters.get("tag"))){
            List<String> addedTags = taggingService.tag(node, parameters.get("tag"));
            if(!addedTags.isEmpty()){
                jcrSessionWrapper.save();
            }

            JSONObject result = new JSONObject();
            result.put("size", node.hasProperty("j:tagList") ? String.valueOf(node.getProperty("j:tagList").getValues().length) : "0");
            JSONArray addedTagsJSON = new JSONArray();
            for (String addedTag : addedTags){
                JSONObject addedTagJSON = new JSONObject();
                addedTagJSON.put("name", addedTag);
                addedTagsJSON.put(addedTagJSON);
            }
            result.put("addedTags", addedTagsJSON);
            return new ActionResult(HttpServletResponse.SC_OK, node.getPath(), result);
        }

        return new ActionResult(HttpServletResponse.SC_OK, node.getPath(), new JSONObject());
    }
}
