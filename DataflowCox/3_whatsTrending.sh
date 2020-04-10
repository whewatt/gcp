bq query "select url, sum(count) from [cox_stream.trending] group by url"
