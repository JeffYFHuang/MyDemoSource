<%@include file="includes/taglib.jsp"%>
<script src="resources/js/editDeleteAnnouncement.js"></script>
<!-- body content starts here -->
<%
	String errorMsg = (String) request.getAttribute("errorMessage");
	String updateMssg = (String) session.getAttribute("updateMsg");
	String deleteMssg = (String) session.getAttribute("deleteMsg");
	String addedAnnMssg = (String) session.getAttribute("addedAnnMsg");
%>
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglabel_announcement"></h1>
			<ol class="breadcrumb-new" id="bread">
				<li><a href="schooldashboard?token=${sessionID}"
					id="school_dashboard"><span class="icglabel_dashboard"></span></a><i
					class="fa fa-chevron-right" aria-hidden="true"></i></li>
				<li><span class="icglabel_announcement"></span></li>
			</ol>
		</section>
		<!-- body  content -->
		<section class="content">
			<!--Start of Student-->
			<section class="content-header-parent">
				<p class="sub_title icglable_createannouncement"></p>
			</section>
			<section class="user_account_mangmet">
				<div class="row">
					<div>
						<div class="txt-12 text-center">
							<label class="control-label"> <%
 	if (errorMsg != null) {
 %> <strong><font color="red"><span class="text-center txt-12 icglabel_invalid_token"></span></font></strong>
								<%
									}
									if (addedAnnMssg != null) {
								%> <strong><font color="green"><span
									class="text-center txt-12 icglabel_announcement_addsuccess"></span></font></strong> <%
 	session.setAttribute("addedAnnMsg", null);
 	}
 %>
							</label>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-4">
						<div class="form-groups">
							<label for="schoolAnnouncementTitle"
								class="control-label icglabel_title"></label> <input type="text"
								class="form-control-user" id="schoolAnnouncementTitle"
								name="schoolAnnouncementTitle" required
								title="Please enter the title" tabindex="1" maxlength="45" /> <span
								class="error-block icglabel_announcementnameempty"
								id="schoolAnnouncementTitle_err"></span>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-8">
						<div class="form-group">
							<label for="schoolAnnouncementDescription"
								class="control-label icglabel_description icglabel_description">
							</label>
							<textarea class="form-control" id="schoolAnnouncementDescription"
								rows="5" name="schoolAnnouncementDescription" required
								title="Please enter the description" tabindex="2"
								maxlength="1000"> </textarea>
							<span class="error-block icglabel_descriptionempty"
								id="schoolAnnouncementDescription_err"></span>

						</div>
					</div>
				</div>
				<div class="row">
					<p hidden="hidden">
						<label id="nameorDesc" class="icglabel_descriptionempty"></label>
					</p>
				</div>
				<div class="row">
					<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
						<button type="button" class="save form-group icglable_post"
							value="Post" id="schoolAnnouncementsSave" tabindex="3"></button>
					</div>
					<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
						<button type="reset" class="cancel form-group icglabel_cancel"
							value="Cancel" id="schoolAnnouncementsCancel" tabindex="4"></button>
					</div>
				</div>
			</section>
			<section class="content-header-parent">
				<c:if test="${fn:length(listOfAnnouncement) gt 0}">
					<p class="sub_title icglabel_announcementlist"></p>
				</c:if>
				<%
					if (updateMssg != null && updateMssg == "icglabel_announcement_updsuccess") {
				%>
				<div class="updateMsg txt-12 text-center"
					style="color: green; display: block;">
					<strong><span class="icglabel_announcement_updsuccess"></span></strong>
				</div>

				<%
					session.setAttribute("updateMsg", null);

					}
					if (deleteMssg != null) {
				%>

				<div class="updateMsg txt-12 text-center"
					style="display: block;">
					<strong><font color="green" class="icglabel_announcement_delsuccess"></font></strong>
				</div>
				<%
					session.setAttribute("deleteMsg", null);
					}
				%>
			</section>
			<p hidden="hidden">
				<input type="text" id="hiddenAnnouncementId">
			</p>
			<c:forEach var="announcement" items="${listOfAnnouncement}">
				<section class="">
					<div class="panel">
						<div class="panel-heading">
							<div class="panel-title">
								<div class="row">
									<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 txt-16">
										<span class="third_subtitle" style="float: left;"><c:out
												value="${announcement.name}" /></span>
										<div class="pull-right">
											<a style="color: black"><img
													onclick="mergeAnnouncementDetails($(this))"
													src="resources/images/unselected_edit_icon.png"
													data-backdrop="static" data-keyboard="true"
													data-toggle="modal" data-target="#editAnnouncementDetails"
													announcement_id="${announcement.announcementId}"
													announcement_name="${announcement.name}"
													announcement_desc="${announcement.description}"
													class="hand" /></a>
											<a style="color: black"
												data-backdrop="static" data-keyboard="true"
												data-toggle="modal" data-target="#deleteAnnouncementDetails"><img
													onclick="mergeAnnouncementDetailsForDelete($(this))"
													src="resources/images/Delete_icon.png"
													announcement_id="${announcement.announcementId}"
													class="hand" /></a> <span class="header-icons"><a
												style="color: black"
												href="#announcementPanel_${announcement.announcementId}"
												data-toggle="collapse"><img class="panelup"
													src="resources/images/arrow_up.png" /></a></span>
										</div>
									</div>
									<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
										<p class="txt-12">
											<c:out value="${announcement.updatedDate}" />
										</p>
									</div>
								</div>
								<hr class="panelhr" />
							</div>
						</div>

						<div class="panel-body collapse in"
							id="announcementPanel_${announcement.announcementId}">
							<div class="panelContent">
									<pre class="panel-pre font-arial"><c:out value="${announcement.description}" />
								</pre>
								
							</div>
						</div>
					</div>
				</section>
			</c:forEach>
		</section>
	</div>
</div>
<div class="modal fade" id="deleteAnnouncementDetails" tabindex=-1
	role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title"></h4>
			</div>
			<div class="modal-body">
				<div class="row">
					<div class="col-md-12">
						<span class="ui-icon ui-icon-alert"
							style="float: left; margin: 12px 12px 20px 0;"></span><span
							class="icglabel_deleteconfirmation"></span>
					</div>
				</div>
				<div class="row" style="padding: 15px;">
					<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group">
							<button type="button" class="confirm pull-right icglabel_ok"
								data-dismiss="modal" onclick="deleteAnnouncement()"></button>
						</div>
					</div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group">
							<button type="button"
								class="modal-Cancel pull-left icglabel_cancel"
								data-dismiss="modal"></button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- Modal for editable student Accpunt list -->
<div class="modal fade" id="editAnnouncementDetails" role="dialog">
	<div class="modal-dialog" data-keyboard="false" data-backdrop="static">
		<p hidden="hidden">
			<input type="text" id="ann_id">
		</p>
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title"></h4>
			</div>
			<div class="modal-body">
					<div class="row">
						<div class="col-md-12">
							<div class="col-md-12">
								<div class="form-groups">
									<label for="editAnntName"
										class="control-label icglabel_announcementname"></label> <input
										type="text" class="form-control-user" id="editAnntName"
										name="editAnntName" required
										title="Please enter Announcement Name" tabindex="1"
										maxlength="45" /><span
										class="error-block icglabel_announcementnameempty"
										id="editAnntName_err"></span>
								</div>
							</div>
						</div>
						<div class="col-md-12">
							<div class="col-md-12">
								<div class="form-group">
									<label for="editAnntDesc"
										class="control-label icglabel_description"></label>
									<textarea class="form-control" id="editAnntDesc" rows="5"
										name="editAnntDesc" required
										title="Please enter the description" tabindex="2"
										maxlength="1000"> </textarea>
									<span class="error-block icglabel_descriptionempty"
										id="editAnntName_err2"></span>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
							<div class="form-group">
								<button type="button" class="confirm pull-right icglabel_update"
									id="updatebutton"></button>
							</div>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
							<div class="form-group">
								<button type="button"
									class="modal-Cancel pull-left icglabel_cancel"
									data-dismiss="modal" id="popupCancelButton"></button>
							</div>
						</div>
					</div>
			</div>
		</div>
	</div>
</div>