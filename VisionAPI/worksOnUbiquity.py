#!/usr/bin/env python

# Copyright 2016 Google Inc. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

def label_file(path, file):
    import io
    from google.cloud.vision import types
    from google.cloud import vision

    client = vision.ImageAnnotatorClient()
    
    file_name = path + '/' + file

    # Loads the image into memory
    with io.open(file_name, 'rb') as image_file:
        content = image_file.read()

    image = types.Image(content=content)

    # Performs label detection on the image file
    response = client.label_detection(image=image)
    labels = response.label_annotations

    found_cat_or_dog = 0
    for label in labels:
        if label.description == 'dog':
	    found_cat_or_dog = 1
            if file_name.find(label.description) > 0:
                print(file_name + ',' + label.description + ',' + 'matched')
	    else:
                print(file_name + ',' + label.description + ',' + 'mismatched')
        else:
            if label.description == 'cat':
                found_cat_or_dog = 1
                if file_name.find(label.description) > 0:
                    print(file_name + ',' + label.description + ',' + 'matched')
		else:
                    print(file_name + ',' + label.description + ',' + 'mismatched')
	    
    if not(found_cat_or_dog):
        print(file_name + ',' + 'Not cat or dog' + ',' + 'mismatched')

def run_quickstart():
    # [START vision_quickstart]
    import os

    path = "/usr/local/google/home/whewatt/Desktop/VisionAPI/train/"
    dirs = os.listdir( path )

    for file in dirs:
        label_file(path, file)   
    
    #file_name = os.path.join(
    #    os.path.dirname(__file__),
    #    '/usr/local/google/home/whewatt/Desktop/VisionAPI/train/dog.998.jpg')

   
    # [END vision_quickstart]


if __name__ == '__main__':
    run_quickstart()