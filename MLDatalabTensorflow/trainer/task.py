import argparse
import pandas as pd
import tensorflow as tf
from tensorflow.contrib.learn.python.learn import learn_runner

#Path to training data
df = pd.read_csv(
  filepath_or_buffer='https://storage.googleapis.com/vijay-public/boston_housing/housing.data.txt',
  delim_whitespace=True,
  names=["CRIM","ZN","INDUS","CHAS","NOX","RM","AGE","DIS","RAD","TAX","PTRATIO","B","LSTAT","MEDV"])

#Column descriptions
#CRIM: per capita crime rate by town
#ZN: proportion of residential land zoned for lots over 25,000 sq.ft.
#INDUS: proportion of non-retail business acres per town
#CHAS: Charles River dummy variable (= 1 if tract bounds river; 0 otherwise)
#NOX: nitric oxides concentration (parts per 10 million)
#RM: average number of rooms per dwelling
#AGE: proportion of owner-occupied units built prior to 1940
#DIS: weighted distances to five Boston employment centres
#RAD: index of accessibility to radial highways
#TAX: full-value property-tax rate per $10,000
#PTRATIO: pupil-teacher ratio by town
#MEDV: Median value of owner-occupied homes - What we will try to predict
FEATURES = ["CRIM", "ZN", "INDUS", "NOX", "RM",
            "AGE", "DIS", "TAX", "PTRATIO"]
LABEL = "MEDV"

feature_cols = [tf.contrib.layers.real_valued_column(k)
                  for k in FEATURES] #list of Feature Columns

#An Estimator is what actually implements your training, eval and prediction loops
def generate_estimator(output_dir):
  return tf.contrib.learn.DNNRegressor(feature_columns=feature_cols,
                                            hidden_units=[10, 10],
                                            model_dir=output_dir)

#The input function returns a (features, label) tuple
def generate_input_fn(data_set):
    def input_fn():
      features = {k: tf.constant(data_set[k].values) for k in FEATURES}
      labels = tf.constant(data_set[LABEL].values)
      return features, labels
    return input_fn

#An experiment is passed to Cloud ML; encapsulates an estimator and an input function
def generate_experiment_fn(output_dir):
  return tf.contrib.learn.Experiment(
    generate_estimator(output_dir),
    train_input_fn=generate_input_fn(df),
    eval_input_fn=generate_input_fn(df),
    train_steps=1000,
    eval_steps=1000
  )

######CLOUD ML ENGINE BOILERPLATE CODE BELOW######
if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  # Input Arguments
  parser.add_argument(
      '--output_dir',
      help='GCS location to write checkpoints and export models',
      required=True
  )
  parser.add_argument(
        '--job-dir',
        help='this model ignores this field, but it is required by gcloud',
        default='junk'
    )
  args = parser.parse_args()
  arguments = args.__dict__
  output_dir = arguments.pop('output_dir')

  learn_runner.run(generate_experiment_fn, output_dir)
