<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Price Quote</title>
</head>

<body>
<h3>Welcome to the Acme Car Insurance App</h3>
<p>
  <s:form action="FlowAction">
  	<s:hidden name="_eventId" value="submit"/>
    <s:textfield label="Please enter your age" name="age"/>
    <s:submit value="Submit Age"/>
  </s:form>
</p>
</body>
</html>
