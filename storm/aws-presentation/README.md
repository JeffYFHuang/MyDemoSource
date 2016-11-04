# HTML Presentation	

## Assumptions:

* You are running a NIX flavor or OS or have cygwin installed

To get a copy:

```
git clone https://bitbucket.org/qanderson/aws-presentation.git
```
## Must install

* Ruby, and Ruby Gem
* http://infews.github.com/keydown, install instructions: https://github.com/infews/keydown

## Usage

Edit the presentation by editing the markdown files (*.md). Detailed instructions for keydown are here: https://github.com/infews/keydown
Markdown Syntax guides can be found here: http://en.wikipedia.org/wiki/Markdown

## Build

To build:
In the project directory:
```
./build.sh
```
To deploy, follow the instructions here: http://s3tools.org/s3cmd

Then run:
```
./deploy.sh
```

Note: deployment only required if you want to deploy to S3, you can run locally just by opening the HTML files in the target dir using your browser
