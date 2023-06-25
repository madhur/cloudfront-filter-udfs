## User Defined function(UDF) to filter cloudfront IP Addresses from AWS Athena Logs

This user defined function allows you to query AWS Athena logs weather or not if the IP address belongs to IP Range.

Check out my blog article [User Defined function(UDF) to filter cloudfront IP Addresses from AWS Athena Logs](https://www.madhur.co.in/blog/2022/12/10/aws-athena-user-defined-function-cloudfront-filter.html)

Once deployed to AWS Lambda as `cloudfront-filter-udfs`, it can be used as in the Athena query:

```sql
USING EXTERNAL FUNCTION IsCloudFrontIP(ip varchar)
RETURNS varchar
LAMBDA 'cloudfront-filter-udfs'
SELECT  request_url, approx_percentile(target_processing_time, 0.99) as p99
FROM "alb_logs"."alb_ext_logs"
where year = '2023'
	and month = '06'
	and day = '14'
	and IsCloudFrontIP(client_ip)='false'
    and target_status_code='200'
	group by request_url order by p99 desc

```