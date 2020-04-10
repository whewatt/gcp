/**
 * Responds to any HTTP request that can provide a "message" field in the body.
 *
 * @param {!Object} req Cloud Function request context.
 * @param {!Object} res Cloud Function response context.
 */


function handleGET (req, res) {
  // Do something with the GET request
  res.status(200).send('This is the ConneXt Cloud Function - please send JSON via POST!');
}

function handlePUT (req, res) {
  // Do something with the PUT request
  res.status(200).json('This is the ConneXt Cloud Function - please send JSON via POST!');
}

function handlePOST (req, res) {
  // Do something with the POST request
    switch (req.get('content-type')) {
    case 'application/json':
        
    // Imports the Google Cloud client library
    const PubSub = require('@google-cloud/pubsub');
        
	const Buffer = require('safe-buffer').Buffer;
    
     // Your Google Cloud Platform project ID
    const projectId = 'whewatt-sandbox';

    // Creates a client
    const pubsub = new PubSub({projectId: projectId,});

    //TO DO: EDIT YOUR TOPIC BELOW
    const topicName = "projects/whewatt-sandbox/topics/cox_stream";
    const topic = pubsub.topic(topicName);
    const publisher = topic.publisher();
	
    console.log('about to loop > ' + req.body.data.length);
	for (var i = 0; i < req.body.data.length; i++) {
		console.log('in the loop');
		const attributes = req.body.data[i];
		//attributes.currentArticle = " " + attributes.currentArticle;
		attributes.currentArticle = attributes.currentArticle.toString();
		attributes.maxArticle = attributes.maxArticle.toString();
		attributes.metered = attributes.metered.toString();
		attributes.privateMode = attributes.privateMode.toString();
		console.log("attributes> " + attributes);

		const data = 'From Cloud Func';
	    console.log("data> " + data);
	
	    const dataBuffer = Buffer.from(data);
		console.log('about to publish');
	    publisher
	  		.publish(dataBuffer, attributes)
		  	.then(results => {
				const messageId = results;
				console.log(`Message ${messageId} published.`);
				res.status(200).json('Accepted JSON via POST!');
			})
			.catch(err => {
	 		   console.error('ERROR:', err);
			});
	}
	console.log('leaving loop');
	break;
        
   case 'text/plain':
      message = req.body;
      res.status(200).json('Please send JSON via POST!');
      break;
  }
  console.log('leaving handlePost');
  //console.log(req.body.message.toString());
  //res.status(200).json('Accepted JSON via POST!');
}

exports.publishConnext = (req, res) => {
  let message;
  
  switch (req.method) {
    case 'GET':
      handleGET(req, res);
      break;
    case 'PUT':
      handlePUT(req, res);
      break;
    case 'POST':
      handlePOST(req, res);
      break;
    default:
      res.status(500).send({ error: 'Something blew up!' });
      break;
  }
};