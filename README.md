This is the Target Gene Notebook source-code repository.

# to build TGN backend against git repository:

  docker build -t targetgenenotebook/tgn https://github.com/targetgenenotebook/tgn.git

# to build against CWD Dockerfile

  docker build -t targetgenenotebook/tgn .

# to run

docker run -d -p 5678:8080 -v /path/to/dbs:/tgn_dbs targetgenenotebook/tgn

       where,
	5678 is the local-machine port which will be serving content
	/path/to/dbs is the local-machine directory holding the TGN SQLite databases

	An example TGN database can be downloaded here:
	https://www.dropbox.com/sh/7d19ig86ue3rvb1/AAC5GGlf-gELmd47VWAkMw7aa?dl=0
	(support tools for creating TGN databases will be uploaded here soon)

	The TGN GUI can be accessed with the Chrome browser as:

	    http://server:5678/

	    where,
		server is the location of the machine running docker

