FROM git.voltras.net:5005/devops/repo/maven:3.9.4-sapmachine-21 AS builder
ARG VERSION
ENV VERSION=$VERSION

ARG PROJECT_ARTIFACTID
ENV PROJECT_ARTIFACTID=$PROJECT_ARTIFACTID

ARG JAVA_PACKAGE
ENV JAVA_PACKAGE=$JAVA_PACKAGE

ENV TZ=Asia/Jakarta
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN mkdir -p /home/app

COPY src /home/app/src
COPY pom.xml /home/app
COPY ci_settings.xml /home/app

#deploy maven
WORKDIR /home/app
RUN mvn -s ci_settings.xml clean package
RUN mvn clean javadoc:jar source:jar deploy -s ci_settings.xml
RUN mvn -s ci_settings.xml compile javadoc:jar source:jar exec:java -Dexec.mainClass="com.voltras.fluttergenerator.FlutterGenerator" -Dexec.args="$PROJECT_ARTIFACTID $JAVA_PACKAGE generatedflutter/ $VERSION"
RUN mkdir -p /home/app/generatedflutter
WORKDIR  /home/app/generatedflutter


FROM git.voltras.net:5005/devops/repo/flutter:20.04-flutter3.13.1-stable AS buildflutter
ARG PACKAGENAME
ENV PACKAGENAME=$PACKAGENAME

RUN mkdir -p /home/app/generatedflutter
COPY --from=builder /home/app/generatedflutter /home/app/generatedflutter
WORKDIR  /home/app/generatedflutter
RUN flutter create --template=package $PACKAGENAME
WORKDIR /home/app/generatedflutter/temp_tobe_removed
RUN wget http://172.16.10.86:8088/documentations/flutter-generator/PubspecAdjuster.jar


FROM git.voltras.net:5005/devops/repo/maven:3.9.4-sapmachine-21 AS stage3
ARG PACKAGENAME
ENV PACKAGENAME=$PACKAGENAME

COPY --from=buildflutter /home/app/generatedflutter /home/app/generatedflutter
WORKDIR /home/app/generatedflutter/temp_tobe_removed
RUN java -cp PubspecAdjuster.jar com.voltras.utilities.PubspecAdjuster ../$PACKAGENAME/pubspec.yaml dependencies dev_dependencies version


FROM git.voltras.net:5005/devops/repo/flutter:20.04-flutter3.13.1-stable AS stage4
ARG PACKAGENAME
ENV PACKAGENAME=$PACKAGENAME

ARG VERSION
ENV VERSION=$VERSION

ARG ADMIN_EMAIL
ENV ADMIN_EMAIL=$ADMIN_EMAIL

ARG ADMIN_NAME
ENV ADMIN_NAME=$ADMIN_NAME

ARG ADMIN_PASSWORD
ENV ADMIN_PASSWORD=$ADMIN_PASSWORD


COPY --from=stage3 /home/app/generatedflutter /home/app/generatedflutter
WORKDIR /home/app/generatedflutter/$PACKAGENAME
RUN flutter pub get && flutter packages pub run build_runner build --delete-conflicting-outputs

#deploy to pub
WORKDIR /home/app/
RUN echo "PUSHING $PACKAGENAME Ver $VERSION TO PUB"
RUN mkdir -p git/
WORKDIR /home/app/git
RUN git ls-remote --heads https://$ADMIN_NAME:$ADMIN_PASSWORD@git.voltras.net/pub/$PACKAGENAME.git refs/heads/main 2>/dev/null | grep main >/dev/null
SHELL ["/bin/bash", "-c"]
RUN if [ "$?" == "1" ] ; then mkdir -p $PACKAGENAME/ && cd $PACKAGENAME/ && git init && git remote add origin https://git.voltras.net/pub/$PACKAGENAME && git checkout -b main ; else git clone https://$ADMIN_NAME:$ADMIN_PASSWORD@git.voltras.net/pub/$PACKAGENAME.git && cd $PACKAGENAME/ && git checkout main ; fi
RUN mkdir -p ../temp/ && if [[ -d ".git" ]] ; then mv .git* ../temp/ ; fi && if [[ -f "README.md" ]] ; then mv README.md ../temp/ ; fi
RUN if [[ -d "lib" ]] ; then rm -rf lib/ ; fi
RUN cp -r ../generatedflutter/$PACKAGENAME/ . && cd $PACKAGENAME/
WORKDIR /home/app/git/$PACKAGENAME
RUN if [[ -d "../temp/.git" ]] ; then cp -r ../temp/.git* . ; fi && if [[ -f "../temp/README.md" ]] ; then cp ../temp/README.md . ; fi
RUN git config --global user.email $ADMIN_EMAIL && git config --global user.name $ADMIN_NAME
RUN git add . && git commit -m "VER $VERSION" && git tag -f -a $VERSION -m "TAG $VERSION"
RUN git push --atomic -u origin main $VERSION