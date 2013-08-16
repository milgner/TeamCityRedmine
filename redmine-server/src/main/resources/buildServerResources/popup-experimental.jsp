<jsp:useBean id="issue" scope="request" type="jetbrains.buildServer.issueTracker.Issue"/>
<script type='text/javascript'>
 BS.LoadStyleSheetDynamicly(base_uri + "/plugins/redmine/style.css");
</script>
<div class="redmineContainer">
 <h3>${issue.summary}</h3>
 <table cellspacing="0" cellpadding="3" >
   <tbody><tr>
     <td><b>Id:</b></td>
     <td><a href="${issue.url}">${issue.id}</a></td>
   </tr>
   <tr>
     <td><b>Status:</b></td>
     <td>${issue.state}</td>
   </tr>
 </tbody></table>
</div>

