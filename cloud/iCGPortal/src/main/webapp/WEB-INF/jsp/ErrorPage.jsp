
<%@include file="includes/taglib.jsp" %> 
 <section class="content-header-error">
	
	     <p class="errorpagetext">The Page Is Not Available</p><br> 
	    <font color="red">${activationCodeExpired}</font> 
	   </section>
	   <p align="center">
	   <font color="red" size="80px">${errorMsg}</font>
	   </p>
<script>
	$('body').addClass('login-page').removeClass('sidebar-mini');
	$('div#main-div').removeClass('wrapper');
</script>