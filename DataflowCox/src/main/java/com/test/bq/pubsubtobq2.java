package com.test.bq;

import java.util.ArrayList;
import java.util.List;

import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;

//Imports added by Wes
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.transforms.windowing.Sessions;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.values.KV;
import org.joda.time.Duration;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class pubsubtobq2 {

	static class ExtractURL extends DoFn<TableRow, String> {
		@ProcessElement
		public void processElement(ProcessContext c){
			TableRow row = c.element();
			String url = (String) row.get("url");
			
			//  "url": "http://www.myajc.com/sports/kempner-gwinnett-place-first-manager-staggered-mall-decline/cCneSxoU0swSYN2rSxyTkO/?ecmp=gwinnco"
			int slashAtEndOfSessionId = url.lastIndexOf('/');
			int slashAtEndOfArticle = url.lastIndexOf('/', slashAtEndOfSessionId - 1);
			
			String articleUrl = url.substring(0, slashAtEndOfArticle);
			c.output(articleUrl);
		}
	}

	static class ComputeTrends extends PTransform<PCollection <String>, PCollection<KV<String, Long>>> {
		@Override
		public PCollection<KV<String, Long>> expand (PCollection<String> actions) {
			return actions
				//TO DO:  set duration of window
				.apply(Window.into(Sessions.withGapDuration(Duration.standardSeconds(15))))
				.apply(Count.perElement());
		}
	}
		
	static class ComputeTopTrends extends PTransform<PCollection<TableRow>, PCollection<TableRow>> {

		@Override
		public PCollection<TableRow> expand (PCollection<TableRow> input) {
			return input
				.apply(ParDo.of(new ExtractURL()))
				.apply(new ComputeTrends())
			    .apply("ConvertTrendsToTableRows", ParDo.of(new DoFn<KV<String,Long>, TableRow>() 
			    {
					private static final long serialVersionUID = 1L;

				  	@ProcessElement
					public void processElement(ProcessContext c) {
				        String url = c.element().getKey();
				        Long count = c.element().getValue();
						
				        TableRow row = new TableRow()
							.set("url", url)
							.set("count", count)
							.set("timestamp", ISODateTimeFormat.dateTime().print(new DateTime(DateTimeZone.UTC)));
						
				        c.output(row);
				      }
				    }));
		}
	}

	public static void main(String[] args) {
	    DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
		
		//TO DO:  set project, temp and staging
	    options.setProject("whewatt-sandbox");
	    options.setTempLocation("gs://cox_stream_df/tmp");
	    options.setStagingLocation("gs://cox_stream_df/staging");
		
	    options.setRunner(DataflowRunner.class);
	    options.setStreaming(true);

	    //TO DO: Topic to pull data from
	    String TOPIC_NAME = "projects/whewatt-sandbox/topics/cox_stream";
	    //TO DO:Big query table location to write to
	    String BQ_DS = "whewatt-sandbox:cox_stream.rawtable_full";
		
		// Build the table schema for the output table.
		List<TableFieldSchema> fields = new ArrayList<>();
		fields.add(new TableFieldSchema().setName("actionId").setType("STRING"));
		fields.add(new TableFieldSchema().setName("actionType").setType("STRING"));
		fields.add(new TableFieldSchema().setName("campaign").setType("STRING"));
		fields.add(new TableFieldSchema().setName("configCode").setType("STRING"));
		fields.add(new TableFieldSchema().setName("connextReferrerType").setType("STRING"));
		fields.add(new TableFieldSchema().setName("conversationName").setType("STRING"));
		fields.add(new TableFieldSchema().setName("currentArticle").setType("INTEGER"));
		fields.add(new TableFieldSchema().setName("interactionType").setType("STRING"));
		fields.add(new TableFieldSchema().setName("maxArticle").setType("STRING"));
		fields.add(new TableFieldSchema().setName("metered").setType("BOOLEAN"));
		fields.add(new TableFieldSchema().setName("privateMode").setType("BOOLEAN"));
		fields.add(new TableFieldSchema().setName("productCode").setType("STRING"));
		fields.add(new TableFieldSchema().setName("relativeTime").setType("STRING"));
		fields.add(new TableFieldSchema().setName("sessionId").setType("STRING"));
		fields.add(new TableFieldSchema().setName("template").setType("STRING"));
		fields.add(new TableFieldSchema().setName("textFromDomElement").setType("STRING"));
		fields.add(new TableFieldSchema().setName("url").setType("STRING"));
		fields.add(new TableFieldSchema().setName("userType").setType("STRING"));
		fields.add(new TableFieldSchema().setName("uuid").setType("STRING"));
		fields.add(new TableFieldSchema().setName("articleCounts").setType("INTEGER"));
		fields.add(new TableFieldSchema().setName("version").setType("STRING"));
		fields.add(new TableFieldSchema().setName("fullVisitorID").setType("STRING"));
		fields.add(new TableFieldSchema().setName("createDateTime").setType("DATETIME"));
		fields.add(new TableFieldSchema().setName("updateDateTime").setType("DATETIME"));
   
	    TableSchema schema = new TableSchema().setFields(fields);
		
	    Pipeline p = Pipeline.create(options);
		
	    PCollection<TableRow> clickstream = p
	    .apply(PubsubIO.readMessagesWithAttributes().fromTopic(TOPIC_NAME))
	    .apply("ConvertDataToTableRows", ParDo.of(new DoFn<PubsubMessage, TableRow>() 
	    {
			private static final long serialVersionUID = 1L;

		  	@ProcessElement
			public void processElement(ProcessContext c) {
		
		        PubsubMessage message = c.element();
		        String actionId = message.getAttribute("actionId");
		        String actionType = message.getAttribute("actionType");
		        String campaign = message.getAttribute("campaign");
		        String configCode = message.getAttribute("configCode");
		        String connextReferrerType = message.getAttribute("connextReferrerType");
		        String conversationName = message.getAttribute("conversationName");
		        String currentArticle = message.getAttribute("currentArticle");
		        String interactionType = message.getAttribute("interactionType");
		        String maxArticle = message.getAttribute("maxArticle");
		        String metered = message.getAttribute("metered");
		        String privateMode = message.getAttribute("privateMode");
		        String productCode = message.getAttribute("productCode");
		        String relativeTime = message.getAttribute("relativeTime");
		        String sessionId = message.getAttribute("sessionId");
		        String template = message.getAttribute("template");
		        String textFromDomElement = message.getAttribute("textFromDomElement");
		        String url = message.getAttribute("url");
		        String userType = message.getAttribute("userType");
		        String uuid = message.getAttribute("uuid");
        
		        System.out.println("Creating table row..");
		        TableRow row = new TableRow()
					.set("actionId", actionId)
					.set("actionType", actionType)
					.set("campaign", campaign)
					.set("configCode", configCode)
					.set("connextReferrerType", connextReferrerType)
					.set("conversationName", conversationName)
					.set("currentArticle", currentArticle)
					.set("interactionType", interactionType)
					.set("maxArticle", maxArticle)
					.set("metered",  metered)
					.set("privateMode", privateMode)
					.set("productCode",  productCode)
					.set("relativeTime",  relativeTime)
					.set("sessionId",  sessionId)
					.set("template", template)
					.set("textFromDomElement", textFromDomElement)
					.set("url", url)
					.set("userType", userType)
					.set("uuid", uuid);            
		        c.output(row);
		      }
		    }));
		
		PCollection<TableRow> trends = clickstream
			.apply(new ComputeTopTrends());

		clickstream.apply("InsertTableRowsToBigQuery",
			BigQueryIO.writeTableRows().to(BQ_DS)
			.withSchema(schema)
			.withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
			.withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));

		List<TableFieldSchema> trendFields = new ArrayList<>();
		trendFields.add(new TableFieldSchema().setName("url").setType("STRING"));
		trendFields.add(new TableFieldSchema().setName("count").setType("INTEGER"));
		trendFields.add(new TableFieldSchema().setName("timestamp").setType("TIMESTAMP"));
		TableSchema trendsSchema = new TableSchema().setFields(trendFields);
		//TO DO:  BigQuery aggregate table name
	    String BQ_TRENDING_DS = "whewatt-sandbox:cox_stream.trending";	
		
		trends.apply("InsertTrendRowsToBigQuery",
			BigQueryIO.writeTableRows().to(BQ_TRENDING_DS)
			.withSchema(schema)
			.withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
			.withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));
				
    	// Run the pipeline
    	p.run().waitUntilFinish();
	}
}