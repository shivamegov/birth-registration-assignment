package digit.service;

import digit.config.BTRConfiguration;
import digit.models.coremodels.UserDetailResponse;
import digit.util.UserUtil;
import digit.web.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {
    private UserUtil userUtils;

    private BTRConfiguration config;

    @Autowired
    public UserService(UserUtil userUtils, BTRConfiguration config) {
        this.userUtils = userUtils;
        this.config = config;
    }

    /**
     * Calls user service to enrich user from search or upsert user
     * @param request
     */
    public void callUserService(BirthRegistrationRequest request){
        request.getBirthRegistrationApplications().forEach(application -> {
            if(!StringUtils.isEmpty(application.getFather().getId()+"")) {
//                enrichUser(application, request.getRequestInfo());
            }else {
                User user = createFatherUser(application);
                application.getFather().setId(Integer.valueOf(upsertUser(user, request.getRequestInfo())));
            }
        });

        request.getBirthRegistrationApplications().forEach(application -> {
            if(!StringUtils.isEmpty(application.getMother().getId()+"")) {
//                enrichUser(application, request.getRequestInfo());
            }
            else {
                User user = createMotherUser(application);
                application.getMother().setId(Integer.valueOf(upsertUser(user, request.getRequestInfo())));
            }
        });
    }

    User createFatherUser(BirthRegistrationApplication application){
        User father = application.getFather();
        User user = User.builder().userName(father.getUserName())
                .name(father.getName())
                .mobileNumber(father.getMobileNumber())
                .emailId(father.getEmailId())
                .altContactNumber(father.getAltContactNumber())
                .tenantId(father.getTenantId())
                .type(father.getType())
                .roles(father.getRoles())
                .build();
//        String tenantId = father.getTenantId();
        return user;
    }

    User createMotherUser(BirthRegistrationApplication application){
        User mother = application.getMother();
        User user = User.builder().userName(mother.getUserName())
                .name(mother.getName())
                .mobileNumber(mother.getMobileNumber())
                .emailId(mother.getEmailId())
                .altContactNumber(mother.getAltContactNumber())
                .tenantId(mother.getTenantId())
                .type(mother.getType())
                .roles(mother.getRoles())
                .build();
//        String tenantId = father.getTenantId();
        return user;
    }
    String upsertUser(User user, RequestInfo requestInfo){

        String tenantId = user.getTenantId();
        org.egov.common.contract.request.User userServiceResponse = null;

        // Search on mobile number as user name
        UserDetailResponse userDetailResponse = searchUser(userUtils.getStateLevelTenant(tenantId),null, user.getMobileNumber());
        if (!userDetailResponse.getUser().isEmpty()) {
            org.egov.common.contract.request.User userFromSearch = userDetailResponse.getUser().get(0);
            log.info(userFromSearch.toString());
            if(!user.getUserName().equalsIgnoreCase(userFromSearch.getUserName())){
                userServiceResponse = updateUser(requestInfo,user,userFromSearch);
            }
            else userServiceResponse = userDetailResponse.getUser().get(0);
        }
        else {
            userServiceResponse = createUser(requestInfo,tenantId,user);
        }

        // Enrich the accountId
        // user.setId(userServiceResponse.getUuid());
        return userServiceResponse.getUuid();
    }


    void enrichUser(BirthRegistrationApplication application, RequestInfo requestInfo){
        String accountIdFather = application.getFather().getId()+"";
        String accountIdMother = application.getMother().getId()+"";
        String tenantId = application.getTenantId();

        UserDetailResponse userDetailResponseFather = searchUser(userUtils.getStateLevelTenant(tenantId),accountIdFather,null);
        UserDetailResponse userDetailResponseMother = searchUser(userUtils.getStateLevelTenant(tenantId),accountIdMother,null);
        if(userDetailResponseFather.getUser().isEmpty())
            throw new CustomException("INVALID_ACCOUNTID","No user exist for the given accountId");

        else application.getFather().setId(Integer.valueOf(userDetailResponseFather.getUser().get(0).getUuid()));

        if(userDetailResponseMother.getUser().isEmpty())
            throw new CustomException("INVALID_ACCOUNTID","No user exist for the given accountId");

        else application.getMother().setId(Integer.valueOf(userDetailResponseMother.getUser().get(0).getUuid()));

    }

    /**
     * Creates the user from the given userInfo by calling user service
     * @param requestInfo
     * @param tenantId
     * @param userInfo
     * @return
     */
    org.egov.common.contract.request.User createUser(RequestInfo requestInfo, String tenantId, User userInfo) {

        userUtils.addUserDefaultFields(userInfo.getMobileNumber(),tenantId, userInfo);
        StringBuilder uri = new StringBuilder(config.getUserHost())
                .append(config.getUserContextPath())
                .append(config.getUserCreateEndpoint());

        CreateUserRequest user = new CreateUserRequest(requestInfo, userInfo);
        log.info(user.getUser().toString());
        UserDetailResponse userDetailResponse = userUtils.userCall(user, uri);


        return userDetailResponse.getUser().get(0);

    }

    /**
     * Updates the given user by calling user service
     * @param requestInfo
     * @param user
     * @param userFromSearch
     * @return
     */
    org.egov.common.contract.request.User updateUser(RequestInfo requestInfo, User user, org.egov.common.contract.request.User userFromSearch) {

        User userBis = new User();
        userBis.setName(user.getName());
        userBis.setActive(true);

        StringBuilder uri = new StringBuilder(config.getUserHost())
                .append(config.getUserContextPath())
                .append(config.getUserUpdateEndpoint());


        UserDetailResponse userDetailResponse = userUtils.userCall(new CreateUserRequest(requestInfo, userBis), uri);

        return userDetailResponse.getUser().get(0);

    }

    /**
     * calls the user search API based on the given accountId and userName
     * @param stateLevelTenant
     * @param accountId
     * @param userName
     * @return
     */
    public UserDetailResponse searchUser(String stateLevelTenant, String accountId, String userName){

        UserSearchRequest userSearchRequest =new UserSearchRequest();
        userSearchRequest.setActive(true);
        userSearchRequest.setUserType("CITIZEN");
        userSearchRequest.setTenantId(stateLevelTenant);

        if(StringUtils.isEmpty(accountId) && StringUtils.isEmpty(userName))
            return null;

        if(!StringUtils.isEmpty(accountId))
            userSearchRequest.setUuid(Collections.singletonList(accountId));

        if(!StringUtils.isEmpty(userName))
            userSearchRequest.setUserName(userName);

        StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
        return userUtils.userCall(userSearchRequest,uri);

    }

    /**
     * calls the user search API based on the given list of user uuids
     * @param uuids
     * @return
     */
//    private Map<String,User> searchBulkUser(List<String> uuids){
//
//        UserSearchRequest userSearchRequest =new UserSearchRequest();
//        userSearchRequest.setActive(true);
//        userSearchRequest.setUserType("CITIZEN");
//
//
//        if(!CollectionUtils.isEmpty(uuids))
//            userSearchRequest.setUuid(uuids);
//
//
//        StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
//        UserDetailResponse userDetailResponse = userUtils.userCall(userSearchRequest,uri);
//        List<org.egov.common.contract.request.User> users = userDetailResponse.getUser();
//
//        if(CollectionUtils.isEmpty(users))
//            throw new CustomException("USER_NOT_FOUND","No user found for the uuids");
//
//        Map<String,User> idToUserMap = users.stream().collect(Collectors.toMap(User::getUuid, Function.identity()));
//
//        return idToUserMap;
//    }

}