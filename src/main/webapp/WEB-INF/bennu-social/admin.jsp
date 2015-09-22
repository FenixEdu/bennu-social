<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

${portal.toolkit()}

<div class="page-header">
   <h1><spring:message code='title.admin'/></h1>
</div>

<p><spring:message code='label.admin.well'/></p>

<c:if test="${! empty errors}">
   <div class="alert alert-danger" role="alert">
      <c:forEach items="${errors}" var="error">
         <p>
            <spring:message code='error.bennu.social.${error}'/>
         </p>
      </c:forEach>
   </div>
</c:if>

<c:if test="${! empty errorMessage}">
   <div class="alert alert-danger" role="alert">
      <p>
         ${errorMessage}
      </p>
   </div>
</c:if>

<c:if test="${! empty messages}">
   <div class="alert alert-success" role="alert">
      <c:forEach items="${messages}" var="message">
         <p>${message}</p>
      </c:forEach>
   </div>
</c:if>

<table class="table">
   <colgroup>
      <col>
      </col>
      <col>
      </col>
      <col>
      </col>
   </colgroup>
   <thead>
      <tr>
         <th><spring:message code='label.api'/></th>
         <th><spring:message code='label.scopes'/></th>
         <th><spring:message code='label.config'/></th>
         <th><spring:message code='label.active'/></th>
      </tr>
   </thead>
   <tbody>
      <c:if test="${! empty github}">
         <tr id="github-api">
            <td><spring:message code='label.api.github'/></td>
            <td>${github.bindedScopes}</td>
            <td>
               <button type="button" class="btn btn-default" data-toggle="modal" data-target="#githubModal"><spring:message code='label.config'/></button>
            </td>
            <td>
               <input type="checkbox" id="githubapiActive" name="githubapiActive" value="true" onchange='updateActive(this)' api='${github.externalId}' url='${pageContext.request.contextPath}/bennu-social-admin/saveActive/${github.externalId}'
               <c:out value="${github.active ? 'checked' : ''}"/>
               >
            </td>
         </tr>
      </c:if>

      <c:if test="${! empty linkedin}">
         <tr id="linkedin-api">
            <td><spring:message code='label.api.linkedin'/></td>
            <td>${linkedin.bindedScopes}</td>
            <td>
               <button type="button" class="btn btn-default" data-toggle="modal" data-target="#linkedinModal"><spring:message code='label.config'/></button>
            </td>
            <td>
               <input type="checkbox" id="linkedinapiActive" name="linkedinapiActive" value="true" onchange='updateActive(this)' api='${linkedin.externalId}' url='${pageContext.request.contextPath}/bennu-social-admin/saveActive/${linkedin.externalId}'
               <c:out value="${linkedin.active ? 'checked' : ''}"/>
               >
            </td>
         </tr>
      </c:if>

      <c:if test="${! empty facebook}">
         <tr id="facebook-api">
            <td><spring:message code='label.api.facebook'/></td>
            <td>${facebook.bindedScopes}</td>
            <td>
               <button type="button" class="btn btn-default" data-toggle="modal" data-target="#facebookModal"><spring:message code='label.config'/></button>
            </td>
            <td>
               <input type="checkbox" id="facebookapiActive" name="facebookapiActive" value="true" onchange='updateActive(this)' api='${facebook.externalId}' url='${pageContext.request.contextPath}/bennu-social-admin/saveActive/${facebook.externalId}'
               <c:out value="${facebook.active ? 'checked' : ''}"/>
               >
            </td>
         </tr>
      </c:if>

      <c:if test="${! empty twitter}">
         <tr id="twitter-api">
            <td><spring:message code='label.api.twitter'/></td>
            <td>${twitter.bindedScopes}</td>
            <td>
               <button type="button" class="btn btn-default" data-toggle="modal" data-target="#twitterModal"><spring:message code='label.config'/></button>
            </td>
            <td>
               <input type="checkbox" id="twitterapiActive" name="twitterapiActive" value="true" onchange='updateActive(this)' api='${twitter.externalId}' url='${pageContext.request.contextPath}/bennu-social-admin/saveActive/${twitter.externalId}'
               <c:out value="${twitter.active ? 'checked' : ''}"/>
               >
            </td>
         </tr>
      </c:if>

      <c:if test="${! empty flickr}">
         <tr id="flickr-api">
            <td><spring:message code='label.api.flickr'/></td>
            <td>${flickr.bindedScopes}</td>
            <td>
               <button type="button" class="btn btn-default" data-toggle="modal" data-target="#flickrModal"><spring:message code='label.config'/></button>
            </td>
            <td>
               <input type="checkbox" id="flickrapiActive" name="flickrapiActive" value="true" onchange='updateActive(this)' api='${flickr.externalId}' url='${pageContext.request.contextPath}/bennu-social-admin/saveActive/${flickr.externalId}'
               <c:out value="${flickr.active ? 'checked' : ''}"/>
               >
            </td>
         </tr>
      </c:if>

      <c:if test="${! empty dropbox}">
         <tr id="dropbox-api">
            <td><spring:message code='label.api.dropbox'/></td>
            <td>${dropbox.bindedScopes}</td>
            <td>
               <button type="button" class="btn btn-default" data-toggle="modal" data-target="#dropboxModal"><spring:message code='label.config'/></button>
            </td>
            <td>
               <input type="checkbox" id="dropboxapiActive" name="dropboxrapiActive" value="true" onchange='updateActive(this)' api='${dropbox.externalId}' url='${pageContext.request.contextPath}/bennu-social-admin/saveActive/${dropbox.externalId}'
               <c:out value="${dropbox.active ? 'checked' : ''}"/>
               >
            </td>
         </tr>
      </c:if>

      <c:if test="${! empty bitbucket}">
         <tr id="bitbucket-api">
            <td><spring:message code='label.api.bitbucket'/></td>
            <td>${bitbucket.bindedScopes}</td>
            <td>
               <button type="button" class="btn btn-default" data-toggle="modal" data-target="#bitbucketModal"><spring:message code='label.config'/></button>
            </td>
            <td>
               <input type="checkbox" id="bitbucketapiActive" name="bitbucketapiActive" value="true" onchange='updateActive(this)' api='${bitbucket.externalId}' url='${pageContext.request.contextPath}/bennu-social-admin/saveActive/${bitbucket.externalId}'
               <c:out value="${bitbucket.active ? 'checked' : ''}"/>
               >
            </td>
         </tr>
      </c:if>

      <c:if test="${! empty google}">
         <tr id="google-api">
            <td><spring:message code='label.api.google'/></td>
            <td>${google.bindedScopes}</td>
            <td>
               <button type="button" class="btn btn-default" data-toggle="modal" data-target="#googleModal"><spring:message code='label.config'/></button>
            </td>
            <td>
               <input type="checkbox" id="googleapiActive" name="googleapiActive" value="true" onchange='updateActive(this)' api='${google.externalId}' url='${pageContext.request.contextPath}/bennu-social-admin/saveActive/${google.externalId}'
               <c:out value="${google.active ? 'checked' : ''}"/>
               >
            </td>
         </tr>
      </c:if>
   </tbody>
</table>

<!-- Modal -->
<spring:message code="label.api.client.id" var="clientID"/>
<spring:message code="label.api.client.secret" var="clientSecret"/>

<div class="modal fade" id="githubModal" tabindex="-1" role="dialog" aria-labelledby="githubModalLabel">
   <div class="modal-dialog" role="document">
      <div class="modal-content">
         <form:form method="POST" class="form-horizontal" action="${pageContext.request.contextPath}/bennu-social-admin/configAPI/${github.externalId}" commandName="github">
            ${csrf.field()}

            <div class="modal-header">
               <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
               <h4 class="modal-title" id="githubModalLabel"><spring:message code="label.api.config.title" arguments="Github"/></h4>
               <small><spring:message code="label.api.config.context"/></small>
            </div>
            <div class="modal-body">
                  <p><spring:message code='label.api.config.well'/></p>

               <div class="form-group">
                  <label class="col-sm-4 control-label"><spring:message code='label.callback.url'/></label>
                  <div class="col-sm-8">
                     <p class="form-control-static">${github.getCallbackURL()}</p>
                  </div>
               </div>
               <div class="form-group">
                  <label class="col-sm-4 control-label"><spring:message code='label.scopes'/></label>
                  <div class="col-sm-8">
                     <p class="form-control-static">${github.bindedScopes}
                     <br>
                       <span class="help-block"><spring:message code='label.api.config.info'/></span></p>
                  </div>
               </div>


               <div class="form-group">
                  <label for="githubClientId" class="col-sm-4 control-label"><spring:message code='label.api.client.id'/></label>
                  <div class="col-sm-8">
                     <form:input class="form-control" placeholder='${clientID}' path="clientId"/>
                  </div>
               </div>
               <div class="form-group">
                  <label for="githubClientSecret" class="col-sm-4 control-label"><spring:message code='label.api.client.secret'/></label>
                  <div class="col-sm-8">
                     <form:input class="form-control" placeholder='${clientSecret}' path="clientSecret"/>
                    <span class="help-block"><spring:message code='label.api.config.help'/>github.</span>
                  </div>
               </div>

            </div>
            <div class="modal-footer">
               <button type="button" class="btn btn-danger pull-left" <c:out value="${github.getGithubUserSet().size() > 0 ? '' : 'disabled'}"/> autocomplete="off" url="${pageContext.request.contextPath}/bennu-social-admin/revokeAll/${github.externalId}" onclick="revokeAll(this)"=><spring:message code="button.revoke.all"/></button>
               <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>
               <button type="submit" class="btn btn-primary"><spring:message code="button.save"/></button>
            </div>
         </form:form>
      </div>
   </div>
</div>

<div class="modal fade" id="linkedinModal" tabindex="-1" role="dialog" aria-labelledby="linkedinModalLabel">
   <div class="modal-dialog" role="document">
      <div class="modal-content">
         <form:form method="POST" class="form-horizontal" action="${pageContext.request.contextPath}/bennu-social-admin/configAPI/${linkedin.externalId}" commandName="linkedin">
            ${csrf.field()}

            <div class="modal-header">
               <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
               <h4 class="modal-title" id="linkedinModalLabel"><spring:message code="label.api.config.title" arguments="Linkedin"/></h4>
               <small><spring:message code="label.api.config.context"/></small>
            </div>
            <div class="modal-body">
                  <p><spring:message code='label.api.config.well'/></p>

               <div class="form-group">
                  <label class="col-sm-4 control-label"><spring:message code='label.callback.url'/></label>
                  <div class="col-sm-8">
                     <p class="form-control-static">${linkedin.getCallbackURL()}</p>
                  </div>
               </div>
               <div class="form-group">
                  <label class="col-sm-4 control-label"><spring:message code='label.scopes'/></label>
                  <div class="col-sm-8">
                     <p class="form-control-static">${linkedin.bindedScopes}
                     <br>
                       <span class="help-block"><spring:message code='label.api.config.info'/></span></p>
                  </div>
               </div>


               <div class="form-group">
                  <label for="githubClientId" class="col-sm-4 control-label"><spring:message code='label.api.client.id'/></label>
                  <div class="col-sm-8">
                     <form:input class="form-control" placeholder='${clientID}' path="clientId"/>
                  </div>
               </div>
               <div class="form-group">
                  <label for="githubClientSecret" class="col-sm-4 control-label"><spring:message code='label.api.client.secret'/></label>
                  <div class="col-sm-8">
                     <form:input class="form-control" placeholder='${clientSecret}' path="clientSecret"/>
                    <span class="help-block"><spring:message code='label.api.config.help'/>linkedin.</span>
                  </div>
               </div>

            </div>
            <div class="modal-footer">
               <button type="button" class="btn btn-danger pull-left" <c:out value="${linkedin.getLinkedinUserSet().size() > 0 ? '' : 'disabled'}"/> autocomplete="off" url="${pageContext.request.contextPath}/bennu-social-admin/revokeAll/${linkedin.externalId}" onclick="revokeAll(this)"=><spring:message code="button.revoke.all"/></button>
               <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>
               <button type="submit" class="btn btn-primary"><spring:message code="button.save"/></button>
            </div>
         </form:form>
      </div>
   </div>
</div>

<div class="modal fade" id="facebookModal" tabindex="-1" role="dialog" aria-labelledby="facebookModalLabel">
   <div class="modal-dialog" role="document">
      <div class="modal-content">
         <form:form method="POST" class="form-horizontal" action="${pageContext.request.contextPath}/bennu-social-admin/configAPI/${facebook.externalId}" commandName="facebook">
            ${csrf.field()}

            <div class="modal-header">
               <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
               <h4 class="modal-title" id="facebookModalLabel"><spring:message code="label.api.config.title" arguments="Facebook"/></h4>
               <small><spring:message code="label.api.config.context"/></small>
            </div>
            <div class="modal-body">
                  <p><spring:message code='label.api.config.well'/></p>

               <div class="form-group">
                  <label class="col-sm-4 control-label"><spring:message code='label.callback.url'/></label>
                  <div class="col-sm-8">
                     <p class="form-control-static">${facebook.getCallbackURL()}</p>
                  </div>
               </div>
               <div class="form-group">
                  <label class="col-sm-4 control-label"><spring:message code='label.scopes'/></label>
                  <div class="col-sm-8">
                     <p class="form-control-static">${facebook.bindedScopes}
                     <br>
                       <span class="help-block"><spring:message code='label.api.config.info'/></span></p>
                  </div>
               </div>


               <div class="form-group">
                  <label for="githubClientId" class="col-sm-4 control-label"><spring:message code='label.api.client.id'/></label>
                  <div class="col-sm-8">
                     <form:input class="form-control" placeholder='${clientID}' path="clientId"/>
                  </div>
               </div>
               <div class="form-group">
                  <label for="githubClientSecret" class="col-sm-4 control-label"><spring:message code='label.api.client.secret'/></label>
                  <div class="col-sm-8">
                     <form:input class="form-control" placeholder='${clientSecret}' path="clientSecret"/>
                    <span class="help-block"><spring:message code='label.api.config.help'/>.</span>
                  </div>
               </div>

            </div>
            <div class="modal-footer">
               <button type="button" class="btn btn-danger pull-left" <c:out value="${facebook.getFacebookUserSet().size() > 0 ? '' : 'disabled'}"/> autocomplete="off" url="${pageContext.request.contextPath}/bennu-social-admin/revokeAll/${facebook.externalId}" onclick="revokeAll(this)"=><spring:message code="button.revoke.all"/></button>
               <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>
               <button type="submit" class="btn btn-primary"><spring:message code="button.save"/></button>
            </div>
         </form:form>
      </div>
   </div>
</div>

<div class="modal fade" id="twitterModal" tabindex="-1" role="dialog" aria-labelledby="twitterModalLabel">
   <div class="modal-dialog" role="document">
      <div class="modal-content">
         <form:form method="POST" class="form-horizontal" action="${pageContext.request.contextPath}/bennu-social-admin/configAPI/${twitter.externalId}" commandName="twitter">
            ${csrf.field()}

            <div class="modal-header">
               <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
               <h4 class="modal-title" id="twitterModalLabel"><spring:message code="label.api.config.title" arguments="Twitter"/></h4>
               <small><spring:message code="label.api.config.context"/></small>
            </div>
            <div class="modal-body">
                  <p><spring:message code='label.api.config.well'/></p>

               <div class="form-group">
                  <label class="col-sm-4 control-label"><spring:message code='label.callback.url'/></label>
                  <div class="col-sm-8">
                     <p class="form-control-static">${twitter.getCallbackURL()}</p>
                  </div>
               </div>
               <div class="form-group">
                  <label class="col-sm-4 control-label"><spring:message code='label.scopes'/></label>
                  <div class="col-sm-8">
                     <p class="form-control-static">${twitter.bindedScopes}
                     <br>
                       <span class="help-block"><spring:message code='label.api.config.info'/></span></p>
                  </div>
               </div>


               <div class="form-group">
                  <label for="githubClientId" class="col-sm-4 control-label"><spring:message code='label.api.client.id'/></label>
                  <div class="col-sm-8">
                     <form:input class="form-control" placeholder='${clientID}' path="clientId"/>
                  </div>
               </div>
               <div class="form-group">
                  <label for="githubClientSecret" class="col-sm-4 control-label"><spring:message code='label.api.client.secret'/></label>
                  <div class="col-sm-8">
                     <form:input class="form-control" placeholder='${clientSecret}' path="clientSecret"/>
                    <span class="help-block"><spring:message code='label.api.config.help'/>.</span>
                  </div>
               </div>

            </div>
            <div class="modal-footer">
               <button type="button" class="btn btn-danger pull-left" <c:out value="${twitter.getTwitterUserSet().size() > 0 ? '' : 'disabled'}"/> autocomplete="off" url="${pageContext.request.contextPath}/bennu-social-admin/revokeAll/${twitter.externalId}" onclick="revokeAll(this)"=><spring:message code="button.revoke.all"/></button>
               <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>
               <button type="submit" class="btn btn-primary"><spring:message code="button.save"/></button>
            </div>
         </form:form>
      </div>
   </div>
</div>

<div class="modal fade" id="flickrModal" tabindex="-1" role="dialog" aria-labelledby="flickrModalLabel">
   <div class="modal-dialog" role="document">
      <div class="modal-content">
         <form:form method="POST" class="form-horizontal" action="${pageContext.request.contextPath}/bennu-social-admin/configAPI/${flickr.externalId}" commandName="flickr">
            ${csrf.field()}

            <div class="modal-header">
               <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
               <h4 class="modal-title" id="flickrModalLabel"><spring:message code="label.api.config.title" arguments="Flickr"/></h4>
               <small><spring:message code="label.api.config.context"/></small>
            </div>
            <div class="modal-body">
                  <p><spring:message code='label.api.config.well'/></p>

               <div class="form-group">
                  <label class="col-sm-4 control-label"><spring:message code='label.callback.url'/></label>
                  <div class="col-sm-8">
                     <p class="form-control-static">${flickr.getCallbackURL()}</p>
                  </div>
               </div>
               <div class="form-group">
                  <label class="col-sm-4 control-label"><spring:message code='label.scopes'/></label>
                  <div class="col-sm-8">
                     <p class="form-control-static">${flickr.bindedScopes}
                     <br>
                       <span class="help-block"><spring:message code='label.api.config.info'/></span></p>
                  </div>
               </div>


               <div class="form-group">
                  <label for="githubClientId" class="col-sm-4 control-label"><spring:message code='label.api.client.id'/></label>
                  <div class="col-sm-8">
                     <form:input class="form-control" placeholder='${clientID}' path="clientId"/>
                  </div>
               </div>
               <div class="form-group">
                  <label for="githubClientSecret" class="col-sm-4 control-label"><spring:message code='label.api.client.secret'/></label>
                  <div class="col-sm-8">
                     <form:input class="form-control" placeholder='${clientSecret}' path="clientSecret"/>
                    <span class="help-block"><spring:message code='label.api.config.help'/>.</span>
                  </div>
               </div>

            </div>
            <div class="modal-footer">
               <button type="button" class="btn btn-danger pull-left" <c:out value="${flickr.getFlickrUserSet().size() > 0 ? '' : 'disabled'}"/> autocomplete="off" url="${pageContext.request.contextPath}/bennu-social-admin/revokeAll/${flickr.externalId}" onclick="revokeAll(this)"=><spring:message code="button.revoke.all"/></button>
               <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>
               <button type="submit" class="btn btn-primary"><spring:message code="button.save"/></button>
            </div>
         </form:form>
      </div>
   </div>
</div>

<div class="modal fade" id="dropboxModal" tabindex="-1" role="dialog" aria-labelledby="dropboxModalLabel">
   <div class="modal-dialog" role="document">
      <div class="modal-content">
         <form:form method="POST" class="form-horizontal" action="${pageContext.request.contextPath}/bennu-social-admin/configAPI/${dropbox.externalId}" commandName="dropbox">
            ${csrf.field()}

            <div class="modal-header">
               <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
               <h4 class="modal-title" id="dropboxModalLabel"><spring:message code="label.api.config.title" arguments="Dropbox"/></h4>
               <small><spring:message code="label.api.config.context"/></small>
            </div>
            <div class="modal-body">
                  <p><spring:message code='label.api.config.well'/></p>

               <div class="form-group">
                  <label class="col-sm-4 control-label"><spring:message code='label.callback.url'/></label>
                  <div class="col-sm-8">
                     <p class="form-control-static">${dropbox.getCallbackURL()}</p>
                  </div>
               </div>
               <div class="form-group">
                  <label class="col-sm-4 control-label"><spring:message code='label.scopes'/></label>
                  <div class="col-sm-8">
                     <p class="form-control-static">${dropbox.bindedScopes}
                     <br>
                       <span class="help-block"><spring:message code='label.api.config.info'/></span></p>
                  </div>
               </div>


               <div class="form-group">
                  <label for="githubClientId" class="col-sm-4 control-label"><spring:message code='label.api.client.id'/></label>
                  <div class="col-sm-8">
                     <form:input class="form-control" placeholder='${clientID}' path="clientId"/>
                  </div>
               </div>
               <div class="form-group">
                  <label for="githubClientSecret" class="col-sm-4 control-label"><spring:message code='label.api.client.secret'/></label>
                  <div class="col-sm-8">
                     <form:input class="form-control" placeholder='${clientSecret}' path="clientSecret"/>
                    <span class="help-block"><spring:message code='label.api.config.help'/>.</span>
                  </div>
               </div>

            </div>
            <div class="modal-footer">
               <button type="button" class="btn btn-danger pull-left" <c:out value="${dropbox.getDropboxUserSet().size() > 0 ? '' : 'disabled'}"/> autocomplete="off" url="${pageContext.request.contextPath}/bennu-social-admin/revokeAll/${dropbox.externalId}" onclick="revokeAll(this)"=><spring:message code="button.revoke.all"/></button>
               <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>
               <button type="submit" class="btn btn-primary"><spring:message code="button.save"/></button>
            </div>
         </form:form>
      </div>
   </div>
</div>

<div class="modal fade" id="bitbucketModal" tabindex="-1" role="dialog" aria-labelledby="bitbucketModalLabel">
   <div class="modal-dialog" role="document">
      <div class="modal-content">
         <form:form method="POST" class="form-horizontal" action="${pageContext.request.contextPath}/bennu-social-admin/configAPI/${bitbucket.externalId}" commandName="bitbucket">
            ${csrf.field()}

            <div class="modal-header">
               <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
               <h4 class="modal-title" id="bitbucketModalLabel"><spring:message code="label.api.config.title" arguments="Bitbucket"/></h4>
               <small><spring:message code="label.api.config.context"/></small>
            </div>
            <div class="modal-body">
                  <p><spring:message code='label.api.config.well'/></p>

               <div class="form-group">
                  <label class="col-sm-4 control-label"><spring:message code='label.callback.url'/></label>
                  <div class="col-sm-8">
                     <p class="form-control-static">${bitbucket.getCallbackURL()}</p>
                  </div>
               </div>
               <div class="form-group">
                  <label class="col-sm-4 control-label"><spring:message code='label.scopes'/></label>
                  <div class="col-sm-8">
                     <p class="form-control-static">${bitbucket.bindedScopes}
                     <br>
                       <span class="help-block"><spring:message code='label.api.config.info'/></span></p>
                  </div>
               </div>

               <div class="form-group">
                  <label for="githubClientId" class="col-sm-4 control-label"><spring:message code='label.api.client.id'/></label>
                  <div class="col-sm-8">
                     <form:input class="form-control" placeholder='${clientID}' path="clientId"/>
                  </div>
               </div>
               <div class="form-group">
                  <label for="githubClientSecret" class="col-sm-4 control-label"><spring:message code='label.api.client.secret'/></label>
                  <div class="col-sm-8">
                     <form:input class="form-control" placeholder='${clientSecret}' path="clientSecret"/>
                    <span class="help-block"><spring:message code='label.api.config.help'/>.</span>
                  </div>
               </div>

            </div>
            <div class="modal-footer">
               <button type="button" class="btn btn-danger pull-left" <c:out value="${bitbucket.getBitbucketUserSet().size() > 0 ? '' : 'disabled'}"/> autocomplete="off" url="${pageContext.request.contextPath}/bennu-social-admin/revokeAll/${bitbucket.externalId}" onclick="revokeAll(this)"=><spring:message code="button.revoke.all"/></button>
               <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>
               <button type="submit" class="btn btn-primary"><spring:message code="button.save"/></button>
            </div>
         </form:form>
      </div>
   </div>
</div>

<div class="modal fade" id="googleModal" tabindex="-1" role="dialog" aria-labelledby="googleModalLabel">
   <div class="modal-dialog" role="document">
      <div class="modal-content">
         <form:form method="POST" class="form-horizontal" action="${pageContext.request.contextPath}/bennu-social-admin/configAPI/${google.externalId}" commandName="google">
            ${csrf.field()}

            <div class="modal-header">
               <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
               <h4 class="modal-title" id="googleModalLabel"><spring:message code="label.api.config.title" arguments="Google"/></h4>
               <small><spring:message code="label.api.config.context"/></small>
            </div>
            <div class="modal-body">
                  <p><spring:message code='label.api.config.well'/></p>

               <div class="form-group">
                  <label class="col-sm-4 control-label"><spring:message code='label.callback.url'/></label>
                  <div class="col-sm-8">
                     <p class="form-control-static">${google.getCallbackURL()}</p>
                  </div>
               </div>
               <div class="form-group">
                  <label class="col-sm-4 control-label"><spring:message code='label.scopes'/></label>
                  <div class="col-sm-8">
                     <p class="form-control-static">${google.bindedScopes}
                     <br>
                       <span class="help-block"><spring:message code='label.api.config.info'/></span></p>
                  </div>
               </div>

               <div class="form-group">
                  <label for="googleClientId" class="col-sm-4 control-label"><spring:message code='label.api.client.id'/></label>
                  <div class="col-sm-8">
                     <form:input class="form-control" placeholder="${clientID}" path="clientId"/>
                  </div>
               </div>
               <div class="form-group">
                  <label for="googleClientSecret" class="col-sm-4 control-label"><spring:message code='label.api.client.secret'/></label>
                  <div class="col-sm-8">
                     <form:input class="form-control" placeholder='${clientSecret}' path="clientSecret"/>
                    <span class="help-block"><spring:message code='label.api.config.help'/>.</span>
                  </div>
               </div>

            </div>
            <div class="modal-footer">
               <button type="button" class="btn btn-danger pull-left" <c:out value="${google.getGoogleUserSet().size() > 0 ? '' : 'disabled'}"/> autocomplete="off" url="${pageContext.request.contextPath}/bennu-social-admin/revokeAll/${google.externalId}" onclick="revokeAll(this)"=><spring:message code="button.revoke.all"/></button>
               <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>
               <button type="submit" class="btn btn-primary"><spring:message code="button.save"/></button>
            </div>
         </form:form>
      </div>
   </div>
</div>

<script type="text/javascript">
   $.ajaxSetup({headers: {'${csrf.headerName}': '${csrf.token}'}});

   function updateActive(checkbox){
     var checked = $(checkbox).is(":checked");
     var url = $(checkbox).attr('url');
     $.ajax({
       type: "POST",
       data: "active=" + checked,
       url: url
     });
   }

   function revokeAll(btn){
    var url = $(btn).attr('url');

    $.ajax({
      type: "GET",
      url: url
    });
   }

</script>
