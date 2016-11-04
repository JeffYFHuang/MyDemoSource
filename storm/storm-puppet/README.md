Puppet Storm
==============================================================

Puppet module for configuration of storm clusters

A fork from <https://github.com/phobos182/storm-puppet>, adapted to work with a vagrant based development cluster for the purposes of the Storm cookbook.

Requirements
------------
You must have your own internal repository and include the 3 packages
compiled for Debian / Ubuntu (storm, libjzmq, libzmq0). These can be found at:

* <https://bitbucket.org/qanderson/storm-deb-packaging/downloads/libjzmq_2.1.7_amd64.deb>
* <https://bitbucket.org/qanderson/storm-deb-packaging/downloads/storm_0.8.1_all.deb>
* <https://bitbucket.org/qanderson/storm-deb-packaging/downloads/libzmq0_2.1.7_amd64.deb>

Install these using the following command:

	dpkg -i [XXX.deb]
	
More information is available from: <https://bitbucket.org/qanderson/storm-deb-packaging> 

Puppet requirements

* Hiera (<http://puppetlabs.com/blog/first-look-installing-and-using-hiera/>)

Usage
-----

The module has a few features. We do not specify individual ports for supervisors. We specify a start port, and a number of workers. Puppet will then iterate over the port list, and create the ports in the storm.yaml file for you. This reduces the headache with manually specifying ports. 

This module also uses Hiera backing for variable discovery. This allows you to setup different clusters with slightly changed settings very easily. An example hiera.yaml scope configuration is listed below.

Example Hiera Scoping (hiera.yaml)

    ---
    :backends:  - yaml
    :hierarchy: - %{hostname}
                - %{environment}/%{cluster}/%{calling_module}
                - %{environment}/%{calling_module}
                - %{environment}
                - common
    :yaml:
      :datadir: '/etc/puppet/hieradata'

With this scoping you should be able to setup Storm clusters by environment, and arbitrary 'cluster' tag. Host specific settings are in the highest scope, followed by cluster / module specific settings. Here is an example node.pp file, and storm.yaml file to specify a production storm cluster with overridden zookeeper / nimbus settings.

Node Definition (node.pp)

    #_ STORM _#
    node /storm[1-9]/ {
      $cluster = 'storm1'
      include storm::supervisor
    }
    
    node 'nimbus1' {
      $cluster = 'storm1'
      include storm::nimbus
      include storm::ui
    }

Hiera Configuration for Storm1 cluster (/etc/puppet/hieradata/production/storm1/storm.yaml)

    ---
    #_ ZOOKEEPER _#
    storm_zookeeper_servers:
      - 'zoo1'
      - 'zoo2'
      - 'zoo3'
    
    #_ NIMBUS _#
    nimbus_host: 'nimbus1'
    
    #_ SUPERVISOR _#
    supervisor_workers: '50'

You can use hiera to override memory settings by host, or by cluster. It's all up to your hiera.yaml scoping resolution. If you have any questions, or improvements. Please submit a pull request / issue.

Thanks!
