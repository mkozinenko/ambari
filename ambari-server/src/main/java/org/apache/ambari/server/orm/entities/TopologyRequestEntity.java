/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.orm.entities;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import java.util.Collection;

@Entity
@Table(name = "topology_request")
@TableGenerator(name = "topology_request_id_generator", table = "ambari_sequences",
                pkColumnName = "sequence_name", valueColumnName = "sequence_value",
                pkColumnValue = "topology_request_id_seq", initialValue = 0)
@NamedQueries({
  @NamedQuery(name = "TopologyRequestEntity.findByCluster", query = "SELECT req FROM TopologyRequestEntity req WHERE req.clusterName = :clusterName")
})
public class TopologyRequestEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "topology_request_id_generator")
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "action", length = 255, nullable = false)
  private String action;

  @Column(name = "cluster_name", length = 100, nullable = false)
  private String clusterName;

  @Column(name = "bp_name", length = 100, nullable = false)
  private String blueprintName;

  @Column(name = "cluster_properties")
  @Basic(fetch = FetchType.LAZY)
  @Lob
  private String clusterProperties;

  @Column(name = "cluster_attributes")
  @Basic(fetch = FetchType.LAZY)
  @Lob
  private String clusterAttributes;

  @Column(name = "description", length = 1024, nullable = false)
  private String description;

  @OneToMany(mappedBy = "topologyRequestEntity", cascade = CascadeType.ALL)
  private Collection<TopologyHostGroupEntity> topologyHostGroupEntities;

  @OneToOne(mappedBy = "topologyRequestEntity", cascade = CascadeType.ALL)
  private TopologyLogicalRequestEntity topologyLogicalRequestEntity;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public String getBlueprintName() {
    return blueprintName;
  }

  public void setBlueprintName(String blueprintName) {
    this.blueprintName = blueprintName;
  }

  public String getClusterProperties() {
    return clusterProperties;
  }

  public void setClusterProperties(String clusterProperties) {
    this.clusterProperties = clusterProperties;
  }

  public String getClusterAttributes() {
    return clusterAttributes;
  }

  public void setClusterAttributes(String clusterAttributes) {
    this.clusterAttributes = clusterAttributes;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Collection<TopologyHostGroupEntity> getTopologyHostGroupEntities() {
    return topologyHostGroupEntities;
  }

  public void setTopologyHostGroupEntities(Collection<TopologyHostGroupEntity> topologyHostGroupEntities) {
    this.topologyHostGroupEntities = topologyHostGroupEntities;
  }

  public TopologyLogicalRequestEntity getTopologyLogicalRequestEntity() {
    return topologyLogicalRequestEntity;
  }

  public void setTopologyLogicalRequestEntity(TopologyLogicalRequestEntity topologyLogicalRequestEntity) {
    this.topologyLogicalRequestEntity = topologyLogicalRequestEntity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TopologyRequestEntity that = (TopologyRequestEntity) o;

    if (!id.equals(that.id)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
