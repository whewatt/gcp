# App Engine Flexible Environment .NET Sample

WARNING:  The codelab instructions have apparently changed since we built this

From https://codelabs.developers.google.com/codelabs/cloud-app-engine-aspnetcore/#0

Build a .NET core app, put it in a Docker container and deploy to App Engine.

## Setup

Install dotnet CLI

## Create .NET app


    $ mkdir AppEngineDotNet
	$ cd AppEngineDotNet
	$ dotnet new web

## Change Default Listening Port

The default port in .NET is 5000.  That won't work for App Engine. 

In Program.cs :

    public static IWebHost BuildWebHost(string[] args) =>
        WebHost.CreateDefaultBuilder(args)
            .UseStartup<Startup>()
			.UseUrls("http://*:8080")  <== Add This Line
            .Build();


## Run the app

    $ dotnet run

## Publish and Dockerize the app

    $ dotnet publish -c Release
	$ cd bin/Release/netcoreapp2.0/publish/
	$ touch Dockerfile

Enter the following contents into the Dockerfile

	FROM gcr.io/google-appengine/aspnetcore:2.0
	ADD ./ /app                                                                                                                                                                                           
	ENV ASPNETCORE_URLS=http://*:${PORT}                                                                                                                                                                 
	WORKDIR /app                                                                                                                                                                                           
	ENTRYPOINT [ "dotnet", "AppEngineDotNet.dll" ]


## Deploy to App Engine

    $ gcloud beta app gen-config
	$ gcloud app deploy --version v0

gen-config creates the app.yaml file
The actual deploy step may take 20 mintues or more.  (For some reason, Flexible env deployments can be like this.)
