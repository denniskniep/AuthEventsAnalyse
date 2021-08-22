from flask import Flask
from flask_restful import reqparse, abort, Api, Resource
import joblib
import pandas as pd
import numpy as np

app = Flask(__name__)
api = Api(app)

parser = reqparse.RequestParser()
parser.add_argument('username', type=str)
parser.add_argument('successful', type=bool)
parser.add_argument('failedLoginsSinceLastSuccessful', type=int)
parser.add_argument('userWorksInThisCountry', type=bool)
parser.add_argument('isOnVacation', type=bool)

model = joblib.load("RiskyAuthenticationEventPredictionWithRandomForestClassifier.joblib")

class Predict(Resource):
    def post(self):
        args = parser.parse_args()
        featureNames = ['successful', 'failedLoginsSinceLastSuccessful','userWorksInThisCountry','isOnVacation']
        features = [args['successful'],
                    args['failedLoginsSinceLastSuccessful'],
                    args['userWorksInThisCountry'],
                    args['isOnVacation']]

        test_data = pd.DataFrame([features], columns=featureNames)
        X_test = pd.get_dummies(test_data)         
       
        predictions = model.predict(X_test)
        return {
                "username": args['username'], 
                "risky": predictions.tolist()[0]
                }, 200
        

##
## Actually setup the Api resource routing here
##
api.add_resource(Predict, '/predict')

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0")