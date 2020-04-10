/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.storage;

// [START storage_quickstart]
// Imports the Google Cloud client library
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage.BucketListOption;
import com.google.api.gax.paging.Page;
import java.util.Iterator;

public class QuickstartSample {
  public static void main(String... args) throws Exception {
    // Instantiates a client
    Storage storage = StorageOptions.getDefaultInstance().getService();

    String prefix = "";
    Page<Bucket> buckets = storage.list(BucketListOption.pageSize(100),
    BucketListOption.prefix(prefix));
    Iterable<Bucket> bucketList = buckets.iterateAll();
    for (Bucket b : bucketList){
      System.out.println(b.getName());
    }
   }
}
// [END storage_quickstart]
