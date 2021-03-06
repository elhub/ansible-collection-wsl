# ${elhub_module_name} deployment

Ansible scripts in this repository can be used to deploy a particular release of the ${elhub_module_name} module to an environment.

## Getting Started

### Prerequisites

* Python 3.6+
* Ansible 2.10+
* <!-- TODO any other pre-reqs -->

#### Ansible Dependencies

The playbook depends on some Ansible roles and collections.
See [requirements.yml](requirements.yml) for the list of dependencies.

To install the required dependencies on the controller node run `ansible-galaxy install -r requirements.yml --force`.

The dependencies will be installed to the [galaxy](galaxy) directory.

### Usage

#### Deploying to OCI

**Resolving the inventory**:

Running `ansible-playbook inventories.yml -e "elhub_env=<target_env>"` will create a static inventory file in `ini` format in the [inventories](inventories) directory for the specified `target_env`.

These inventory files are then used with the `ansible-playbook` in the following way:

```
ansible-playbook main.yml \
-i inventories/<target_env>.ini
```

For more information see also [inventory](https://code.elhub.cloud/projects/COM/repos/ansible-collection-commons/browse/roles/inventory) role in `ansible-collection-commons`.

**Deploying from local workstation**:

Describe how to deploy the module from local workstation

**Deploying from TeamCity**:

Describe how deployment is handled differently in TeamCity if applicable

#### Deploying release vs. development version

In order to deploy a release an ansible var `component_version` has to be passed to the `ansible-playbook` command with the version number. If this is not done - a "development / snapshot" version will be deployed instead.

For example, to deploy a release `v1.2.3`, a `component_version` var should be passed to the `ansible-playbook` command:

```
ansible-playbook main.yml \
-e "component_version=1.2.3"
```

#### Logging deployment result

Based on the result of deployment a log entry must be created using the `log_deploy.yml` play:

* PASS - `ansible-playbook log_deploy.yml -e "elhub_env=<target_env>" -e "module_status='DEPLOY COMPLETED'"`
* FAIL - `ansible-playbook log_deploy.yml -e "elhub_env=<target_env>" -e "module_status='DEPLOY FAILED'"`

#### Tying it all together with `deploy-runner.sh`

All the above steps can be executed together with the [deploy-runner.sh](https://code.elhub.cloud/projects/ELH/repos/elhub/browse/scripts/deploy-runner.sh) script.

## Meta

* [Ansible Module Deployment Guidelines](https://docs.elhub.cloud/elhub/elhub/wiki/ansible-module-deployment/)
* [Ansible Sub-System Deployment Guidelines](https://docs.elhub.cloud/elhub/elhub/wiki/ansible-subsystem-deployment/)
* [Ansible Liquibase Deployment Guidelines](https://docs.elhub.cloud/elhub/elhub/wiki/ansible-liquibase-deployment/)
* [Adding Deployment Support For New Environments](https://docs.elhub.cloud/elhub/elhub/wiki/deploys-to-new-environments/)
