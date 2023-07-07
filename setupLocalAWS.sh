echo "create the role"
awslocal iam create-role --role-name post-save-role --assume-role-policy-document "{"Version": "2012-10-17","Statement": [{ "Effect": "Allow", "Principal": {"Service": "lambda.amazonaws.com"}, "Action": "sts:AssumeRole"}]}"
echo "create the policy"
awslocal iam create-policy --policy-name lambda-policy --policy-document file://policy.json
echo "attach the policy to the role"
awslocal iam attach-role-policy --policy-arn arn:aws:iam::000000000000:policy/lambda-policy --role-name post-save-role
echo "create the input queue"
awslocal sqs create-queue --queue-name post-save-queue
echo "create the post-save table"
awslocal dynamodb create-table --table-name post --attribute-definitions AttributeName=pk,AttributeType=S AttributeName=sk,AttributeType=S --key-schema AttributeName=pk,KeyType=HASH AttributeName=sk,KeyType=RANGE --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 --table-class STANDARD
echo "create the function"
awslocal lambda create-function --function-name post-save-lambda --zip-file fileb://target/function.zip --runtime java11 --role arn:aws:iam::000000000000:role/post-save-role --timeout 30
echo "create the source mapping"
awslocal lambda create-event-source-mapping --function-name post-save-lambda --batch-size 5 --maximum-batching-window-in-seconds 60  --event-source-arn arn:aws:sqs:us-east-1:000000000000:post-save-queue