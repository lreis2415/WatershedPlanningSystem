# How to run client side

## Environment setup

1. **Node**: The system was developed using node v16.16 and npm v8.11. So the latter versions are recommended.

2. **Dependencies**: The following command can be used to install all the dependencies listed in *package.json*.
```
npm install
```


## Start server
After successfully installing all the dependencies, the following command can be used to start the front-end server.
```
npm run serve
```
This command will start the local server (e.g., http://localhost:8080), and the client-side pages can be accessed through this link.

## Compiles and minifies for production
The following command can be compile, minify and package the codes for use in production environment.
```
npm run build
```
The generated *dist* folder can be depolyed in a web server (e.g., nginx).


