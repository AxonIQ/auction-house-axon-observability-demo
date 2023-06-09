#!/usr/bin/env bash
#
# Copyright (c) 2023-2023. AxonIQ
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


cd frontend-landing
docker build .
docker build . -t ghcr.io/AxonIQ/axon-observability-landing
docker push ghcr.io/AxonIQ/axon-observability-landing
cd ..

docker push ghcr.io/AxonIQ/axon-observability-service-auctions
docker push ghcr.io/AxonIQ/axon-observability-service-auction-query
docker push ghcr.io/AxonIQ/axon-observability-service-auction-object-registry
docker push ghcr.io/AxonIQ/axon-observability-service-participants
docker push ghcr.io/AxonIQ/axon-observability-service-interfaces
docker push ghcr.io/AxonIQ/axon-observability-service-allinclusive

