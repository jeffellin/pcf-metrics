# Update the revision tag in a POM file
# https://raw.githubusercontent.com/spring-io/concourse-java-scripts/v0.0.2/concourse-java.sh
set_revision_to_pom() {
	[ -n $1 ] || { echo "missing set_revision_to_pom() argument" >&2; return 1; }
	grep -q "<revision>.*</revision>" pom.xml || { echo "missing revision tag" >&2; return 1; }
	sed -ie "s|<revision>.*</revision>|<revision>${1}</revision>|" pom.xml > /dev/null
}

setup_github_access() {
	mkdir -p ~/.ssh

    set +x

	echo "$1"  >> ~/.ssh/known_hosts
    echo "$2" > ~/.ssh/github_key
    chmod 600 ~/.ssh/github_key
    eval $(ssh-agent -s)
    ssh-add ~/.ssh/github_key

    set -x

    ssh-add -l

	git config --global user.email "concourse@example.com"
    git config --global user.name "Concourse CI"

	git remote add upstream "$3"
}

generate_release_notes() { 
	git fetch upstream --tags
	last_tag=$(git for-each-ref refs/tags --sort=-taggerdate --format='%(refname:short)' --count=1 || git rev-list --max-parents=0 HEAD --max-count=1)
    echo "## Changes since release ${last_tag}:" > release.md
    git log ${last_tag}..HEAD --reverse --pretty=format:"'- [%s]($1/commit/%H)'" | grep -v 'ci skip' >> release.md || echo "- No changes found" >> release.md
}

# Generate settings.xml for Maven to point to mirror
# https://raw.githubusercontent.com/MarcialRosales/maven-concourse-pipeline/02_use_corporate_maven_repo/tasks/generate-settings.sh

generate_settings() {
	if [[ -z M2_SETTINGS_CENTRAL_MIRROR_URI ]]; then
		return
	fi

	mkdir -p ${HOME}/.m2/

	echo "Writing settings xml to [${HOME}/.m2/settings.xml]"
	echo "Repository URL: ${M2_SETTINGS_CENTRAL_MIRROR_URI}"
	echo "Repository Username: ${M2_SETTINGS_CORP_REPO_USERNAME}"

	cat > ${HOME}/.m2/settings.xml <<EOF

	<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
       <localRepository>${M2_CACHE}</localRepository>
</settings>

EOF
}