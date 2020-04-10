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

def label_file(filename):
    
    print filename

def run_quickstart():
    # [START vision_quickstart]
    import io
    import os

    # Imports the Google Cloud client library
    # [START migration_import]
    from google.cloud import vision
    from google.cloud.vision import types
    # [END migration_import]

    # Instantiates a client
    # [START migration_client]
    client = vision.ImageAnnotatorClient()
    # [END migration_client]

    # The name of the image file to annotate
    
    #See https://www.tutorialspoint.com/python/os_listdir.htm
    path = "/Users/whewatt/Downloads/test/"
    dirs = os.listdir( path )

    for file in dirs:
        label_file(file)   
    
    file_name = os.path.join(
        os.path.dirname(__file__),
        '/Users/whewatt/Downloads/train/cat.1.jpg')

    # Loads the image into memory
    with io.open(file_name, 'rb') as image_file:
        content = image_file.read()

    image = types.Image(content=content)

    # Performs label detection on the image file
    response = client.label_detection(image=image)
    labels = response.label_annotations

    print('Labels:')
    for label in labels:
        if label.description == 'dog':
            print(file_name + ',' + label.description)
        else:
            if label.description == 'cat':
                print(file_name + ',' + label.description)
                if file_name.find(label.description) > 0:
                    print ("Matched")
            
    # [END vision_quickstart]


if __name__ == '__main__':
    run_quickstart()
