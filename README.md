# AWS-Cognito-Auth-Android

1. Android application sends username and password to the Nodejs server file named cognitoServer.js which verifies user details by accessing any database private to a company and then calls GetOpenIdTokenForDeveloperIdentity API to return an IdentityID and OpenID Token.

2. The IdentityID and OpenID Token are then passed as paramters to the GetCredentialsForIdentity API which is a public API and there is no need to pass any credentials to call this API.

3. The GetCredentialsForIdentity API returns a Credentials Object which contains the required AccessKeyId, SecretKey and SessionToken which are then passed to AmazonDynamoDBClient custructor to initialise DynamoDB access.

4. The same Credentials can be used to access other AWS services depending upon IAM roles set to the cognito identity pool.


# Dependencies

    compile 'com.amazonaws:aws-android-sdk-ddb-mapper:2.2.+'
    compile 'com.amazonaws:aws-android-sdk-core:2.2.+'
    compile 'com.amazonaws:aws-android-sdk-cognito:2.2.+'
    compile 'com.amazonaws:aws-android-sdk-ddb:2.2.+'
    compile 'com.squareup.okhttp3:okhttp:3.6.0'
    compile 'com.amazonaws:aws-android-sdk-cognitoidentityprovider:2.2.+'
