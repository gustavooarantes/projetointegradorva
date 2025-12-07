FROM openjdk:17-jdk-slim

ENV ANDROID_HOME="/sdk"
ENV PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"

RUN apt-get update && apt-get install -y \
  wget \
  unzip \
  git \
  && rm -rf /var/lib/apt/lists/*

RUN mkdir -p $ANDROID_HOME/cmdline-tools \
  && wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip -O /tmp/tools.zip \
  && unzip -q /tmp/tools.zip -d $ANDROID_HOME/cmdline-tools \
  && mv $ANDROID_HOME/cmdline-tools/cmdline-tools $ANDROID_HOME/cmdline-tools/latest \
  && rm /tmp/tools.zip

RUN yes | sdkmanager --licenses > /dev/null

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x gradlew

COPY . .

CMD ["./gradlew", "assembleDebug"]
