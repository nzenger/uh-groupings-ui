package edu.hawaii.its.groupings.access;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hawaii.its.api.controller.GroupingsRestController;
import edu.hawaii.its.api.type.AdminListsHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.client.authentication.SimplePrincipal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Map<String, List<Role>> userMap = new HashMap<>();

    @Autowired
    GroupingsRestController groupingsRestController;

    private static final Log logger = LogFactory.getLog(AuthorizationServiceImpl.class);

    /**
     * Assigns roles to user
     *
     * @param uhUuid   : The UH uuid of the user.
     * @param username : The username of the person to find the user.
     * @return : Returns an array list of roles assigned to the user.
     */
    @Override
    public RoleHolder fetchRoles(String uhUuid, String username) {
        RoleHolder roleHolder = new RoleHolder();
        roleHolder.add(Role.ANONYMOUS);
        roleHolder.add(Role.UH);

        //Determines if user is an owner.
        if (fetchOwner(username)) {
            roleHolder.add(Role.OWNER);
        }

        //Determines if a user is an admin.
        if (fetchAdmin(username)) {
            roleHolder.add(Role.ADMIN);
        }

        List<Role> roles = userMap.get(uhUuid);
        if (roles != null) {
            for (Role role : roles) {
                roleHolder.add(role);
            }
        }
        return roleHolder;
    }

    /**
     * Determines if a user is an owner of any grouping.
     *
     * @param username - uid of user
     * @return true if the person has groupings that they own, otherwise false.
     */
    public boolean fetchOwner(String username) {
        try {
            logger.info("//////////////////////////////");
            Principal principal = new SimplePrincipal(username);

            String groupingAssignmentJson = (String) groupingsRestController.isOwner(principal).getBody();
            if (null != groupingAssignmentJson) {
                Map<String, String> groupingAssignment = OBJECT_MAPPER.readValue(groupingAssignmentJson, Map.class);

                if ("SUCCESS".equals(groupingAssignment.get("resultCode"))) {
                    logger.info("This person is an owner");
                    return true;
                } else {
                    logger.info("This person is not owner");
                }
            }
        } catch (NullPointerException | JsonProcessingException ne) {
            logger.error(ne.getMessage());
        }
        return false;
    }

    /**
     * Determines if a user is an admin in grouping admin.
     *
     * @param username - self-explanatory
     * @return true if the person gets pass the grouping admins check by checking if they can get all the groupings.
     */
    public boolean fetchAdmin(String username) {
        logger.info("//////////////////////////////");
        try {

            Principal principal = new SimplePrincipal(username);
            String adminListHolderJson = (String) groupingsRestController.adminLists(principal).getBody();
            AdminListsHolder adminListsHolder = OBJECT_MAPPER.readValue(adminListHolderJson, AdminListsHolder.class);

            // todo eliminate this entire public function and replace with the isAdmin api call
            if (!(adminListsHolder.getAdminGroup().getMembers().size() == 0)) {
                logger.info("this person is an admin");
                return true;
            } else {
                logger.info("this person is not an admin");
            }
        } catch (Exception e) {
            logger.info("Error in getting admin info. Error message: " + e.getMessage());
        }
        logger.info("//////////////////////////////");
        return false;
    }
}