# jenkins_plugin_particle_build
A Jenkins plugin which sends Particle.io devices the status of a build.

## Installation
- Run "mvn package" in the root directory. A "particle.hpi" file will be generated in the /target directory.
- Copy "particle.hpi" to $JENKINS_HOME/plugins and restart the instance.

## Usage
- Manually configure an access token to use for the plugin in the Jenkins global configuration.
- Add a build step or post build action of "Send build status to a Particle device"
- Set the name of the event to send to the device on build status change.

## Data
- The event sent when used as a **build step** will be:
-     name: <your event name>, data: "BUILDING"
- The event sent when used as a **post build step** will be:
-     name: <your event name>, data: "SUCCESS"
- or:
-     name: <your event name>, data: "FAILURE"
- or:
-     name: <your event name>, data: "NONE"
