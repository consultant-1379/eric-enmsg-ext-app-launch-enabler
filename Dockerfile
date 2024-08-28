ARG ERIC_ENM_SLES_EAP7_IMAGE_NAME=eric-enm-sles-eap7
ARG ERIC_ENM_SLES_EAP7_IMAGE_REPO=armdocker.rnd.ericsson.se/proj-enm
ARG ERIC_ENM_SLES_EAP7_IMAGE_TAG=1.64.0-32

FROM ${ERIC_ENM_SLES_EAP7_IMAGE_REPO}/${ERIC_ENM_SLES_EAP7_IMAGE_NAME}:${ERIC_ENM_SLES_EAP7_IMAGE_TAG}

ARG BUILD_DATE=
ARG IMAGE_BUILD_VERSION=
ARG GIT_COMMIT=
ARG ISO_VERSION=
ARG RSTATE=
ARG SGUSER=232337

LABEL \
com.ericsson.product-number="CXC 999 9999" \
com.ericsson.product-revision=$RSTATE \
enm_iso_version=$ISO_VERSION \
org.label-schema.name="TestSG" \
org.label-schema.build-date=$BUILD_DATE \
org.label-schema.vcs-ref=$GIT_COMMIT \
org.label-schema.vendor="Ericsson" \
org.label-schema.version=$IMAGE_BUILD_VERSION \
org.label-schema.schema-version="1.0.0-rc1"

RUN zypper install -y ERICserviceframework4_CXP9037454 \
    ERICserviceframeworkmodule4_CXP9037453 \
    ERICmodelservice_CXP9030595 \
    ERICmodelserviceapi_CXP9030594 && \
    zypper download ERICbonetanstandaloneui_CXP9034896 && \
    rpm -ivh /var/cache/zypp/packages/enm_iso_repo/ERICbonetanstandaloneui_CXP9034896*rpm --nodeps --noscripts --replacefiles && \
    zypper clean -a

RUN echo "$SGUSER:x:$SGUSER:0: An identity for ext-app-launch-enabler:/nonexistent:/bin/false" >> /etc/passwd && \
     echo "$SGUSER:!::0:::::" >> /etc/shadow

COPY --chown=jboss_user:jboss image_content/external-application-launch-enabler-service-ear-*.ear /ericsson/3pp/jboss/standalone/deployments/
COPY --chown=jboss_user:jboss eap_config/jboss-as.conf /ericsson/3pp/jboss/

ENV ENM_JBOSS_SDK_CLUSTER_ID="extapplaunchenabler" \
    ENM_JBOSS_BIND_ADDRESS="0.0.0.0" \
    GLOBAL_CONFIG="/gp/global.properties"

USER $SGUSER