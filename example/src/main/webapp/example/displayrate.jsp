<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Price Quote</title>
</head>

<body>
<h3>Acme Price Quote</h3>
<p>
  <s:form action="FlowAction" method="get">
    Your monthly premium would be $<s:property value="rate"/>
    <s:submit type="button" name="_eventId" value="startOver" label="Start Over"></s:submit>
    <s:submit type="button" name="_eventId" value="finish" label="Finish"></s:submit>
  </s:form>
</p>
</body>
</html>
