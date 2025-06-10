output "cluster_endpoint" {
  description = "EKS API server endpoint"
  value       = module.eks.cluster_endpoint
}

output "cluster_ca_certificate" {
  description = "Base64-encoded CA certificate for EKS"
  value       = module.eks.cluster_certificate_authority_data
  sensitive   = true
}

output "managed_node_group_arns" {
  description = "The ARNs of your managed node groups"
  value       = module.eks.managed_node_group_arns
}