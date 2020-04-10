# BigQuery ML Explained

## Training data
Create a table called 'data' with the following values

    Row	x	y	 
    1	1	2	 
    2	2	4	 
    3	3	6	 
    4	4	8	 
    5	5	10	 
    6	6	12

## Create model

    create model bqml_tutorial.data_model
    options (model_type='linear_reg') as
    select
    y as label,
    x
    from
    `bqml_tutorial.data` 

## Use the model to predict

    select predicted_label as predicted_y
    from
    ml.predict(model bqml_tutorial.data_model, (
    select 10 as x)) 