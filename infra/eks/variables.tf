variable "aws_region" {
  default = "us-east-1"
}

variable "cluster_name" {
  default = "team-health-eks"
}

variable "vpc_id" {
  type        = string
  description = "VPC to deploy EKS into"
}

variable "subnet_ids" {
  type        = list(string)
  description = "List of subnet IDs for EKS worker nodes"
}

variable "public_subnet_id" {
  description = "A public subnet ID for NAT Gateway"
  type        = string
}