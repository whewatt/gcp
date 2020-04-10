# tensorflow_teaching_examples

From https://github.com/vijaykyr/tensorflow_teaching_examples

This contains a Jupyter notebook that is intended to be run from Datalab.  Look in cloud-ml-housing-prices.ipynb for more instructions.

The work in the notebook can also be done on the local machine.

The notebook demo-cloud-ml-housing-prices.ipynb is a shortened version of the first notebook that makes it easier to tell a demo story.  It will NOT work on Datalab without first running cloud-ml-housing-prices.ipynb

##Training Data
Can be downloaded from https://storage.googleapis.com/vijay-public/boston_housing/housing_train.csv if someone wants to view the data.

The data does NOT have to be downloaded as the tensorflow/Python code references the storage bucket.

##Local Machine
trainer/task.py contains the Tensorflow model

records.json contains feature data for two houses.  Passing this file into the model will result in a prediction of the sale price for each house.

The shell scripts 1_trainGCP.sh, 2_deployModel.sh, 3_predictGPC.sh execute the three step process to train a model, create a model, and use the model for prediction.

##Calling the Model through an API
python predict.py
   OR
./4_predictAPI.sh
