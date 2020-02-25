package com.empanada.app.webservice.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.empanada.app.webservice.exceptions.UserNotFoundException;
import com.empanada.app.webservice.exceptions.UserServiceException;
import com.empanada.app.webservice.io.entity.UserEntity;
import com.empanada.app.webservice.io.repository.UserRepository;
import com.empanada.app.webservice.io.repository.impl.UserRepositoryPagination;
import com.empanada.app.webservice.pagination.Page;
import com.empanada.app.webservice.service.AddressService;
import com.empanada.app.webservice.service.UserService;
import com.empanada.app.webservice.shared.SecurityUtils;
import com.empanada.app.webservice.shared.dto.UserAddressDTO;
import com.empanada.app.webservice.shared.dto.UserBasicInformationDTO;
import com.empanada.app.webservice.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  SecurityUtils utils;

  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  AddressService addressService;

  @Override
  public UserBasicInformationDTO createUser(UserBasicInformationDTO user) throws UserServiceException {
    if (userRepository.findByEmail(user.getEmail()) != null)
      throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());

    ModelMapper mapper = new ModelMapper();
    UserEntity userEntity = generateModelToSave(user);
    userRepository.save(userEntity);
//    new AmazonSES().verifyEmail(mapper.map(userEntity, UserBasicInformationDTO.class));
    return mapper.map(userEntity,UserBasicInformationDTO.class);
  }

  private UserEntity generateModelToSave(final UserBasicInformationDTO user) {
    UserBasicInformationDTO userCopy = cloneUser(user);
    generateUserInfo(userCopy);
    return new ModelMapper().map(userCopy, UserEntity.class);
  }

  private UserBasicInformationDTO cloneUser(UserBasicInformationDTO user) {
    return new ModelMapper().map(user, UserBasicInformationDTO.class);
  }

  private void generateUserInfo(UserBasicInformationDTO user) {
    Assert.notNull(user, "User cannot be null");
    final String publicUserId = utils.generateUserId(SecurityUtils.DEFAULT_LENGTH);
    user.setEmailVerificationToken(SecurityUtils.generateVerificationToken(publicUserId));
    user.setPublicUserId(publicUserId);
    user.setEncryptedPassword(encriptPassword(user.getPassword()));
    generateAddressesId(user.getAddresses());
  }

  private String encriptPassword (String password) {
    return bCryptPasswordEncoder.encode(password);
  }

  private void generateAddressesId(List<UserAddressDTO> addresses) {
    addresses.forEach( address -> 
    address.setAddressId(utils.generateAddressId(SecurityUtils.DEFAULT_LENGTH)));
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UserNotFoundException {
    final UserEntity userLoginDetails = userRepository.findByEmail(email);

    if (userLoginDetails == null)
      throw new UserNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

    // User is a Spring Security BEAN
    return new User(userLoginDetails.getEmail(), userLoginDetails.getEncryptedPassword(),
        userLoginDetails.getEmailVerficationStatus(), true, true, true, new ArrayList<>());
  }

  @Override
  public UserBasicInformationDTO getUserByEmail(String email) throws UserNotFoundException {
    final UserEntity userDetails = userRepository.findByEmail(email);
    if (userDetails == null)
      throw new UserNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

    final UserBasicInformationDTO userDtoInformation = new UserBasicInformationDTO();
    BeanUtils.copyProperties(userDetails, userDtoInformation);

    return userDtoInformation;
  }

  @Override
  public UserBasicInformationDTO getUserByPublicUserId(String userId) throws UserNotFoundException {
    final UserEntity userDetails = userRepository.findByPublicUserId(userId);
    if (userDetails == null)
      throw new UserNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

    final UserBasicInformationDTO userDtoInformation = new UserBasicInformationDTO();
    BeanUtils.copyProperties(userDetails, userDtoInformation);

    return userDtoInformation;
  }

  @Override
  public UserBasicInformationDTO updateUser(String userId, UserBasicInformationDTO user) throws UserNotFoundException {
    final UserEntity userDetails = userRepository.findByPublicUserId(userId);
    if (userDetails == null)
      throw new UserNotFoundException(ErrorMessages.COULD_NOT_UPDATE_RECORD.getErrorMessage());

    userDetails.setFirstName(user.getFirstName());
    userDetails.setLastName(user.getLastName());
    userRepository.save(userDetails);

    final UserBasicInformationDTO userDto = new UserBasicInformationDTO();
    BeanUtils.copyProperties(userDetails, userDto);

    return userDto;
  }

  @Override
  public void deleteUserByPublicUserId(String publicUserId) throws UserNotFoundException {
    final UserEntity userDetails = userRepository.findByPublicUserId(publicUserId);
    if (userDetails == null)
      throw new UserNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

    userRepository.delete(userDetails);
  }

  @Override
  public List<UserBasicInformationDTO> getUsersIndexedByPage(Page page) {
    final UserRepositoryPagination userRepositoryByPagination = new UserRepositoryPagination(userRepository, page);
    final List<UserEntity> userListDetails = userRepositoryByPagination.getUsers();
    return copyModelToResponse(userListDetails);
  }

  private List<UserBasicInformationDTO> copyModelToResponse(List<UserEntity> userListDetails) {
    final List<UserBasicInformationDTO> usersBasicInfo = new ArrayList<>();
    for (final UserEntity user : userListDetails) {
      final UserBasicInformationDTO userBasicInfo = new UserBasicInformationDTO();
      BeanUtils.copyProperties(user, userBasicInfo);
      usersBasicInfo.add(userBasicInfo);
    }

    if (usersBasicInfo.isEmpty())
      return Collections.emptyList();

    return usersBasicInfo;
  }

  @Override
  public void verifyEmailToken(String token) throws UserServiceException {
    try{
      final UserEntity userEntity = findUserByVerificationToken(token);
      if (!SecurityUtils.hasTokenExpired(token))
        saveEmailVerification(userEntity);
    } catch (UserNotFoundException e) {
      throw new UserServiceException("Could not verify user with token ["+token+"]");
    }
  }

  private UserEntity findUserByVerificationToken(String token) throws UserNotFoundException {
    final UserEntity userEntity = userRepository.findByEmailVerificationToken(token);
    if (userEntity == null)
      throw new UserNotFoundException("No user with token ["+token+"]");
    return userEntity;
  }

  private void saveEmailVerification(final UserEntity userEntity) {
    verifyEmailOnUser(userEntity);
    userRepository.save(userEntity);
  }

  private void verifyEmailOnUser(final UserEntity userEntity) {
    userEntity.setEmailVerificationToken(null);
    userEntity.setEmailVerficationStatus(Boolean.TRUE);
  }

}
