<%--

    Copyright © 2016 Instituto Superior Técnico

    This file is part of Bennu Social.

    Bennu Social is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Bennu Social is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Bennu Social.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

${portal.toolkit()}

<div class="page-header">
  <h1><spring:message code='title.social.user'/></h1>
</div>
<p><spring:message code='label.social.user.well'/></p>

<c:if test="${! empty errors}">
  <div class="alert alert-danger" role="alert">
    <c:forEach items="${errors}" var="error">
      <p><spring:message code='error.bennu.social.${error}'/></p>
    </c:forEach>
  </div>
</c:if>

<c:if test="${! empty messages}">
  <div class="alert alert-success" role="alert">
    <c:forEach items="${messages}" var="message">
      <p>${message}</p>
    </c:forEach>
  </div>
</c:if>


<c:if test="${(!empty githubAPI && githubAPI.active) ||
              (!empty linkedinAPI && linkedinAPI.active) ||
              (!empty facebookAPI && facebookAPI.active) ||
              (!empty twitterAPI && twitterAPI.active) ||
              (!empty flickrAPI && flickrAPI.active) ||
              (!empty dropboxAPI && dropboxAPI.active) ||
              (!empty bitbucketAPI && bitbucketAPI.active) ||
              (!empty googleAPI && googleAPI.active)}">

  <table class="table">
    <colgroup>
      <col></col>
    </colgroup>
    <thead>
      <tr>
        <th><spring:message code='label.api'/></th>
      </tr>
    </thead>
    <tbody>
      <c:if test="${! empty githubAPI && githubAPI.active}">
        <tr id="github-api">
          <c:set var="githubAuth" value="${githubAPI.isUserAuthenticated(user)}"/>
          <td>
            <spring:message code='label.api.github'/>
            <button type="button" class="btn btn-default pull-right" data-toggle="modal" data-target="#githubModal"  <c:out value="${githubAuth ? '' : 'disabled'}"/>><spring:message code="button.permission.revoke"/></button>
          </td>
        </tr>
      </c:if>

      <c:if test="${! empty  linkedinAPI && linkedinAPI.active}">
        <tr id=" linkedin-api">
          <c:set var=" linkedinAuth" value="${ linkedinAPI.isUserAuthenticated(user)}"/>
          <td>
            <spring:message code='label.api.linkedin'/>
            <button type="button" class="btn btn-default pull-right" data-toggle="modal" data-target="#linkedinModal"  <c:out value="${ linkedinAuth ? '' : 'disabled'}"/>><spring:message code="button.permission.revoke"/></button>
          </td>
        </tr>
      </c:if>

      <c:if test="${! empty  facebookAPI && facebookAPI.active}">
        <tr id=" facebook-api">
          <c:set var=" facebookAuth" value="${ facebookAPI.isUserAuthenticated(user)}"/>
          <td>
            <spring:message code='label.api.facebook'/>
            <button type="button" class="btn btn-default pull-right" data-toggle="modal" data-target="#facebookModal"  <c:out value="${ facebookAuth ? '' : 'disabled'}"/>><spring:message code="button.permission.revoke"/></button>
          </td>
        </tr>
      </c:if>

      <c:if test="${! empty  twitterAPI && twitterAPI.active}">
        <tr id=" twitter-api">
          <c:set var=" twitterAuth" value="${ twitterAPI.isUserAuthenticated(user)}"/>
          <td>
            <spring:message code='label.api.twitter'/>
            <button type="button" class="btn btn-default pull-right" data-toggle="modal" data-target="#twitterModal"  <c:out value="${ twitterAuth ? '' : 'disabled'}"/>><spring:message code="button.permission.revoke"/></button>
          </td>
        </tr>
      </c:if>

      <c:if test="${! empty  flickrAPI && flickrAPI.active}">
        <tr id=" flickr-api">
          <c:set var=" flickrAuth" value="${ flickrAPI.isUserAuthenticated(user)}"/>
          <td>
            <spring:message code='label.api.flickr'/>
            <button type="button" class="btn btn-default pull-right" data-toggle="modal" data-target="#flickrModal"  <c:out value="${ flickrAuth ? '' : 'disabled'}"/>><spring:message code="button.permission.revoke"/></button>
          </td>
        </tr>
      </c:if>

      <c:if test="${! empty  dropboxAPI && dropboxAPI.active}">
        <tr id=" dropbox-api">
          <c:set var=" dropboxAuth" value="${ dropboxAPI.isUserAuthenticated(user)}"/>
          <td>
            <spring:message code='label.api.dropbox'/>
            <button type="button" class="btn btn-default pull-right" data-toggle="modal" data-target="#dropboxModal"  <c:out value="${ dropboxAuth ? '' : 'disabled'}"/>><spring:message code="button.permission.revoke"/></button>
          </td>
        </tr>
      </c:if>

      <c:if test="${! empty  bitbucketAPI && bitbucketAPI.active}">
        <tr id=" bitbucket-api">
          <c:set var=" bitbucketAuth" value="${ bitbucketAPI.isUserAuthenticated(user)}"/>
          <td>
            <spring:message code='label.api.bitbucket'/>
            <button type="button" class="btn btn-default pull-right" data-toggle="modal" data-target="#bitbucketModal"  <c:out value="${ bitbucketAuth ? '' : 'disabled'}"/>><spring:message code="button.permission.revoke"/></button>
          </td>
        </tr>
      </c:if>

      <c:if test="${! empty googleAPI && googleAPI.active}">
        <tr id=" google-api">
          <c:set var=" googleAuth" value="${ googleAPI.isUserAuthenticated(user)}"/>
          <td>
            <spring:message code='label.api.google'/>
            <button type="button" class="btn btn-default pull-right" data-toggle="modal" data-target="#googleModal"  <c:out value="${ googleAuth ? '' : 'disabled'}"/>><spring:message code="button.permission.revoke"/></button>
          </td>
        </tr>
      </c:if>

    </tbody>
  </table>

</c:if>
<c:if test="${!((!empty githubAPI && githubAPI.active) ||
              (!empty linkedinAPI && linkedinAPI.active) ||
              (!empty facebookAPI && facebookAPI.active) ||
              (!empty twitterAPI && twitterAPI.active) ||
              (!empty flickrAPI && flickrAPI.active) ||
              (!empty dropboxAPI && dropboxAPI.active) ||
              (!empty bitbucketAPI && bitbucketAPI.active) ||
              (!empty googleAPI && googleAPI.active))}">
  <div class="empty"><p><span>No external integrations</span>to display</p></div>
</c:if>

<!-- Modal -->
<div class="modal fade" id="githubModal" tabindex="-1" role="dialog" aria-labelledby="githubModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="githubModalLabel"><spring:message code="button.permission.revoke.confirm.title"/></h4>
      </div>
      <div class="modal-body">
        <p><spring:message code="button.permission.revoke.confirm.text" arguments="Github"/></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>
        <a href="${pageContext.request.contextPath}/bennu-social-user/revoke/${githubAPI.externalId}" type="button" class="btn btn-primary pull-right"><spring:message code="button.permission.revoke.confirm"/></a>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="linkedinModal" tabindex="-1" role="dialog" aria-labelledby="linkedinModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="linkedinModalLabel"><spring:message code="button.permission.revoke.confirm.title"/></h4>
      </div>
      <div class="modal-body">
        <p><spring:message code="button.permission.revoke.confirm.text" arguments="Linkedin"/></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>
        <a href="${pageContext.request.contextPath}/bennu-social-user/revoke/${linkedinAPI.externalId}" type="button" class="btn btn-primary pull-right"><spring:message code="button.permission.revoke.confirm"/></a>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="facebookModal" tabindex="-1" role="dialog" aria-labelledby="facebookModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="facebookModalLabel"><spring:message code="button.permission.revoke.confirm.title"/></h4>
      </div>
      <div class="modal-body">
        <p><spring:message code="button.permission.revoke.confirm.text" arguments="Facebook"/></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>
        <a href="${pageContext.request.contextPath}/bennu-social-user/revoke/${facebookAPI.externalId}" type="button" class="btn btn-primary pull-right"><spring:message code="button.permission.revoke.confirm"/></a>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="twitterModal" tabindex="-1" role="dialog" aria-labelledby="twitterModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="twitterModalLabel"><spring:message code="button.permission.revoke.confirm.title"/></h4>
      </div>
      <div class="modal-body">
        <p><spring:message code="button.permission.revoke.confirm.text" arguments="Twitter"/></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>
        <a href="${pageContext.request.contextPath}/bennu-social-user/revoke/${twitterAPI.externalId}" type="button" class="btn btn-primary pull-right"><spring:message code="button.permission.revoke.confirm"/></a>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="flickrModal" tabindex="-1" role="dialog" aria-labelledby="flickrModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="flickrModalLabel"><spring:message code="button.permission.revoke.confirm.title"/></h4>
      </div>
      <div class="modal-body">
        <p><spring:message code="button.permission.revoke.confirm.text" arguments="Flickr"/></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>
        <a href="${pageContext.request.contextPath}/bennu-social-user/revoke/${flickrAPI.externalId}" type="button" class="btn btn-primary pull-right"><spring:message code="button.permission.revoke.confirm"/></a>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="dropboxModal" tabindex="-1" role="dialog" aria-labelledby="dropboxModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="dropboxModalLabel"><spring:message code="button.permission.revoke.confirm.title"/></h4>
      </div>
      <div class="modal-body">
        <p><spring:message code="button.permission.revoke.confirm.text" arguments="Dropbox"/></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>
        <a href="${pageContext.request.contextPath}/bennu-social-user/revoke/${dropboxAPI.externalId}" type="button" class="btn btn-primary pull-right"><spring:message code="button.permission.revoke.confirm"/></a>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="bitbucketModal" tabindex="-1" role="dialog" aria-labelledby="bitbucketModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="bitbucketModalLabel"><spring:message code="button.permission.revoke.confirm.title"/></h4>
      </div>
      <div class="modal-body">
        <p><spring:message code="button.permission.revoke.confirm.text" arguments="Bitbucket"/></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>
        <a href="${pageContext.request.contextPath}/bennu-social-user/revoke/${bitbucketAPI.externalId}" type="button" class="btn btn-primary pull-right"><spring:message code="button.permission.revoke.confirm"/></a>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="googleModal" tabindex="-1" role="dialog" aria-labelledby="googleModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="googleModalLabel"><spring:message code="button.permission.revoke.confirm.title"/></h4>
      </div>
      <div class="modal-body">
        <p><spring:message code="button.permission.revoke.confirm.text" arguments="Google"/></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>
        <a href="${pageContext.request.contextPath}/bennu-social-user/revoke/${googleAPI.externalId}" type="button" class="btn btn-primary pull-right"><spring:message code="button.permission.revoke.confirm"/></a>
      </div>
    </div>
  </div>
</div>
