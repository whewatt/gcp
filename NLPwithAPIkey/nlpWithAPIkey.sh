curl -X POST   -H "Content-Type: application/json; charset=utf-8"      --data "{
  'encodingType': 'UTF8',
  'document': {
    'type': 'PLAIN_TEXT',
    'content': 'Enjoy your vacation!'
  }
}" "https://language.googleapis.com/v1/documents:analyzeSentiment?key={InsertAPIKeyFromCloudConsoleHere"
